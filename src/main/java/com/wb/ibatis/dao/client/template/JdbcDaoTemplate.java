package com.wb.ibatis.dao.client.template;

import java.sql.Connection;

import com.ibatis.dao.engine.transaction.ConnectionDaoTransaction;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoManager;
import com.wb.ibatis.dao.client.DaoTransaction;

/**
 * @author www
 * @date 2016年1月9日
 * 
 * JDBC模板类，提供了一个方法来访问JDBC连接。
 * JDBC和JTA事务管理都可以使用这个模板，
 * 所有可以使用JDBC连接来进行事务管理的可以使用这个模板，
 * 如iBATIS SQL Maps和Hiber。
 */

public abstract class JdbcDaoTemplate extends DaoTemplate {

	/**
	 * 管理这个Dao实例的DaoManager作为参数
	 * @param daoManager
	 */
	public JdbcDaoTemplate(DaoManager daoManager) {
		super(daoManager);
	}
	
	/**
	 * 获得JDBC连接对象，从当前dao实例运行的Dao事务中获取。
	 * @return
	 */
	protected Connection getConnection() {
		DaoTransaction transaction = daoManager.getTransaction(this);
		if (!(transaction instanceof ConnectionDaoTransaction)) {
			throw new DaoException("The DAO manager of type " + daoManager.getClass().getName() +
			          " cannot supply a JDBC Connection for this template, and is therefore not" +
			          "supported by JdbcDaoTemplate.");
		}
		return ((ConnectionDaoTransaction) trans).getConnection();
	}

}
