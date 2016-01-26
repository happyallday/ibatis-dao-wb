package com.wb.ibatis.dao.engine.transaction.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.common.jdbc.logging.ConnectionLogProxy;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.engine.transaction.ConnectionDaoTransaction;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * JDBC事务
 * 
 */

public class JdbcDaoTransaction implements ConnectionDaoTransaction {

	private static final Log connectionLog = LogFactory.getLog(Connection.class);
	
	private Connection connection;
	
	public JdbcDaoTransaction(DataSource dataSource) {
		try {
			connection = dataSource.getConnection();
			if (connection == null) {
				throw new DaoException("Could not start transaction. Cause: The DataSource returned a null connection.");
			}
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}
			if (connectionLog.isDebugEnabled()) {
				connection = ConnectionLogProxy.newInstance(connection);
			}
		} catch (SQLException e) {
			throw new DaoException("Error starting JDBC transaction. Cause: " + e);
		}
	}
	
	/**
	 * 获取数据库连接对象Connection
	 */
	@Override
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * 提交事务
	 */
	@Override
	public void commit() {
		try {
			try {
				connection.commit();
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			throw new DaoException("Error commiting JDBC transaction. Cause: " + e);
		}
	}

	/**
	 * 回滚事务
	 */
	@Override
	public void rollback() {
		try {
			try {
				connection.rollback();
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			throw new DaoException("Error ending JDBC transaction. Cause: " + e);
		}
	}
}
