package com.wb.ibatis.dao.engine.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wb.ibatis.dao.client.Dao;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoTransaction;
import com.wb.ibatis.dao.engine.transaction.DaoTransactionManager;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * DAO上下文，事务管理器和DAO实例的容器
 * 
 */

public class DaoContext {

	private String id; // context的编号
	private StandardDaoManager daoManager; // context所在的dao管理器
	private DaoTransactionManager transactionManager; // 事务管理器
	
	private ThreadLocal<DaoTransaction> transaction = new ThreadLocal<>(); // 处理事务的本地线程，主要实现线程操作
	private ThreadLocal<DaoTransactionState> state = new ThreadLocal<>(); // 处理事务状态信息的本地线程
	
	private Map<Class<?>, DaoImpl> typeDaoImplMap = new HashMap<>(); // 当前context容器中所有的DaoImpl实例

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StandardDaoManager getDaoManager() {
		return daoManager;
	}

	public void setDaoManager(StandardDaoManager daoManager) {
		this.daoManager = daoManager;
	}

	public DaoTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(DaoTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * 添加DaoImpl，以dao的接口为key保存
	 * @param daoImpl
	 */
	public void addDao(DaoImpl daoImpl) {
		if (typeDaoImplMap.containsKey(daoImpl.getDaoInterface())) {
			throw new DaoException("More than one implemention for '" + daoImpl.getDaoInterface()
					+ "' was configured. Only one implementation per context is allowed");
		}
		typeDaoImplMap.put(daoImpl.getDaoInterface(), daoImpl);
	}
	
	/**
	 * 根据接口，返回代理实例
	 * @param iface
	 * @return
	 */
	public Dao getDao(Class<?> type) {
		DaoImpl impl = typeDaoImplMap.get(type);
		if (impl == null) {
			throw new DaoException("There is no DAO implementation found for " + type + " in this context.");
		}
		return impl.getProxy();
	}
	
	/**
	 * 返回context保存的所有DaoImpl的迭代器
	 * @return
	 */
	public Iterator<DaoImpl> getDaoImpls() {
		return typeDaoImplMap.values().iterator();
	}
	
	/**
	 * 获取DaoTransaction
	 * @return
	 */
	public DaoTransaction getTransaction() {
		startTransaction();
		return transaction.get();
	}
	
	/**
	 * 开始事务，采用单例模式实现事务
	 */
	public void startTransaction() {
		if (state.get() != DaoTransactionState.ACTIVE) {
			DaoTransaction trans = transactionManager.startTransaction();
			transaction.set(trans);
			state.set(DaoTransactionState.ACTIVE);
			daoManager.addContextInTransaction(this);
		}
	}
	
	/**
	 * 提交事务
	 */
	public void commitTransaction() {
		DaoTransaction trans = transaction.get();
		if (state.get() == DaoTransactionState.ACTIVE) {
			transactionManager.commitTransaction(trans);
			state.set(DaoTransactionState.COMMITED);
		} else {
			state.set(DaoTransactionState.INACTIVE);
		}
	}
	
	/**
	 * 结束事务
	 */
	public void endTransaction() {
		DaoTransaction trans = transaction.get();
		if (state.get() == DaoTransactionState.ACTIVE) {
			try {
				transactionManager.rollbackTransaction(trans);
			} finally {
				state.set(DaoTransactionState.ROLLEDBACK);
			}
		} else {
			state.set(DaoTransactionState.INACTIVE);
		}
		transaction.set(null);
	}
}















