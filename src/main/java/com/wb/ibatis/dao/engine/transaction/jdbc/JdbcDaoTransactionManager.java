package com.wb.ibatis.dao.engine.transaction.jdbc;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ibatis.common.jdbc.DbcpConfiguration;
import com.ibatis.common.jdbc.SimpleDataSource;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoTransaction;
import com.wb.ibatis.dao.engine.transaction.DaoTransactionManager;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * JDBC事务管理器
 * 
 */

public class JdbcDaoTransactionManager implements DaoTransactionManager {

	private DataSource dataSource;
	
	/**
	 * 配置事务管理器
	 */
	@Override
	public void configure(Properties properties) {
		if (properties.containsKey("DataSource")) {
			String type = properties.getProperty("DataSource");
			if ("SIMPLE".equals(type)) {
				dataSource = new SimpleDataSource(properties);
			}
			else if ("DBCP".equals(type)) {
				DbcpConfiguration dbcp = new DbcpConfiguration(properties);
				dataSource = dbcp.getDataSource();
			}
			else if ("JNDI".equals(type)) {
				configureJndi(properties);
			}
		}
	}

	/**
	 * 开始事务
	 */
	@Override
	public DaoTransaction startTransaction() {
		return new JdbcDaoTransaction(dataSource);
	}

	/**
	 * 提交事务
	 */
	@Override
	public void commitTransaction(DaoTransaction transaction) {
		transaction.commit();
	}

	/**
	 * 回滚事务
	 */
	@Override
	public void rollbackTransaction(DaoTransaction transaction) {
		transaction.rollback();
	}
	
	/**
	 * JNDI方式配置事务管理器
	 * @param properties
	 */
	private void configureJndi(Properties properties) {
		final String PREFIX = "context.";
		Hashtable<String, String> contextProps = new Hashtable<>();
		Set<String> keySet = properties.stringPropertyNames();
		for (String key: keySet) {
			if (key.startsWith(PREFIX)) {
				contextProps.put(key.substring(PREFIX.length()), properties.getProperty(key));
			}
		}
		
		try {
			InitialContext initialContext = null;
			if (contextProps.isEmpty()) {
				initialContext = new InitialContext();
			} else {
				initialContext = new InitialContext(contextProps);
			}
			dataSource = (DataSource) initialContext.lookup(properties.getProperty("DBJndiContext"));
		} catch (NamingException e) {
			throw new DaoException("There was an error configuring the DataSource from JDNI. Cause: " + e);
		}
	}

}



















