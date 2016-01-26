package com.wb.ibatis.dao.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wb.ibatis.dao.client.Dao;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoManager;
import com.wb.ibatis.dao.client.DaoTransaction;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * iBATIS DAO框架实现的默认Dao管理器类
 * 
 */

public class StandardDaoManager implements DaoManager {
	
	private static final String DAO_EXPLICIT_TX = "__DAO_EXPLICIT_TX"; // 用来标识manager是否开启了事务
	
	private ThreadLocal<String> transactionMode = new ThreadLocal<>(); // 用来标识manager是否开启了事务
	private ThreadLocal<List<DaoContext>> contextInTransactionList = new ThreadLocal<>(); // 开启了事务的DaoContext列表
	
	private Map<String, DaoContext> idContextMap = new HashMap<>(); // 当前manager容器中的所有DaoContext对象，按照DaoContext的id来放置
	private Map<Class<?>, DaoContext> typeContextMap = new HashMap<>(); // 当前manager容器中所有DaoContext对象，按照DaoImpl的接口来放置
	private Map<Dao, DaoImpl> daoImplMap = new HashMap<>(); // 当前manager容器中所有的DaoImpl对象，以DAO实例对象和DAO代理对象为key存了双份

	/**
	 * 添加DaoContext
	 * @param context
	 */
	public void addContext(DaoContext context) {
		// 按id来添加
		if (context.getId() != null && context.getId().length() > 0) {
			if (idContextMap.containsKey(context.getId())) {
				throw new DaoException("There is already a DAO Context with the ID '" + context.getId() + "'.");
			}
			idContextMap.put(context.getId(), context);
		}
		
		// 按类型来添加
		Iterator<DaoImpl> iterator = context.getDaoImpls();
		while (iterator.hasNext()) {
			DaoImpl daoImpl = iterator.next();
			if (typeContextMap.containsKey(daoImpl.getDaoInterface())) {
				typeContextMap.put(daoImpl.getDaoInterface(), null);
			} else {
				typeContextMap.put(daoImpl.getDaoInterface(), context);
			}
			daoImplMap.put(daoImpl.getProxy(), daoImpl);
			daoImplMap.put(daoImpl.getDaoInstance(), daoImpl);
		}
	}
	
	/**
	 * 根据type类型获取一个DAO实例。
	 * @param type 需要返回的DAO实例的接口类型。
	 * @return
	 */
	@Override
	public Dao getDao(Class<?> type) {
		DaoContext context = typeContextMap.get(type);
		if (context == null) {
			throw new DaoException("There is no DAO implementation found for " + type + " in any context."
						+ " If you've registered multiple implementations of this DAO, you must specify"
						+ " the Context ID for the DAO implementation you're looking for using the"
						+ " getDao(Class type, String contextId) method.");
		}
		return context.getDao(type);
	}

	/**
	 * 从指定的context中根据type类型获取一个DAO实例。
	 * @param type 需要返回的DAO实例的接口类型。
	 * @param contextId 指定context的contextId，从这个context中找到需返回DAO实例。
	 * @return
	 */
	@Override
	public Dao getDao(Class<?> type, String contextId) {
		DaoContext context = idContextMap.get(contextId);
		if (context == null) {
			throw new DaoException("There is no Context found with the ID " + contextId + ".");
		}
		return context.getDao(type);
	}

	/**
	 * 获取指定的Dao对象所在的事务对象，如果Dao对象没在事务中，则必会启动一个事务。
	 * @param dao
	 * @return
	 */
	@Override
	public DaoTransaction getTransaction(Dao dao) {
		DaoImpl daoImpl = daoImplMap.get(dao);
		return daoImpl.getDaoContext().getTransaction();
	}

	/**
	 * 开始这个DaoManager所管理的事务，
	 * 如果这个方法没被调用，所有Dao方法会使用"autocommit"。
	 */
	@Override
	public void startTransaction() {
		transactionMode.set(DAO_EXPLICIT_TX);
	}

	/**
	 * 提交这个DaoManager所管理的所有Dao Context中正在被started的事务。
	 */
	@Override
	public void commitTransaction() {
		List<DaoContext> contextList = getContextInTransactionList();
		for (DaoContext context: contextList) {
			context.commitTransaction();
		} 
	}

	/**
	 * 结束这个DaoManager所管理的所有Dao Context中正在被started的事务，
	 * 如果事务还没有成功提交，则会被回滚。
	 */
	@Override
	public void endTransaction() {
		List<DaoContext> contextList = getContextInTransactionList();
		try {
			for (DaoContext context: contextList) {
				context.endTransaction();
			} 
		} finally {
			transactionMode.set(null);
			contextList.clear();
		}
	}

	/**
	 * 当前manager是否开启了事务并没有结束
	 * @return
	 */
	public boolean isExplicitTransaction() {
		return DAO_EXPLICIT_TX.equals(transactionMode.get());
	}
	
	/**
	 * 添加开启了事务的DaoContext
	 * @param context
	 */
	public void addContextInTransaction(DaoContext context) {
		List<DaoContext> contextList = getContextInTransactionList();
		if (!contextList.contains(context)) {
			contextList.add(context);
		}
	}
	
	/**
	 * 返回所有开启了事务的DaoContext列表
	 * @return
	 */
	private List<DaoContext> getContextInTransactionList() {
		List<DaoContext> contextList = contextInTransactionList.get();
		if (contextList == null) {
			contextList = new ArrayList<>();
			contextInTransactionList.set(contextList);
		}
		return contextList;
	}
}
