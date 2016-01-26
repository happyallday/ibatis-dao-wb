package com.wb.ibatis.dao.engine.transaction.sqlmap;

import java.sql.Connection;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.engine.transaction.ConnectionDaoTransaction;

/**
 * @author www
 * @date 2016年1月24日
 * 
 * SQL Map 事务
 * 
 */

public class SqlMapDaoTransaction implements ConnectionDaoTransaction {

	private SqlMapClient client;
	
	public SqlMapDaoTransaction(SqlMapClient client) {
		try {
			client.startTransaction();
			this.client = client;
		} catch (SQLException e) {
			throw new DaoException("Error starting SQL Map transaction. Cause: " + e, e);
		}
	}
	
	public SqlMapClient getSqlMap() {
		return client;
	}

	/**
	 * 获取数据库连接对象Connection
	 */
	@Override
	public Connection getConnection() {
		try {
			return client.getCurrentConnection();
		} catch (SQLException e) {
			throw new DaoException("Error getting connection from SQL Map Client. Cause: " + e, e);
		}
	}

	/**
	 * 提交事务
	 */
	@Override
	public void commit() {
		try {
			client.commitTransaction();
			client.endTransaction();
		} catch (SQLException e) {
			throw new DaoException("Error committing SQL Map transaction. Cause: " + e, e);
		}
	}

	/**
	 * 回滚事务
	 */
	@Override
	public void rollback() {
		try {
			client.endTransaction();
		} catch (SQLException e) {
			throw new DaoException("Error ending SQL Map transaction. Cause: " + e, e);
		}
	}

}
