package com.wb.ibatis.dao.engine.transaction.sqlmap;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoTransaction;
import com.wb.ibatis.dao.engine.transaction.DaoTransactionManager;

/**
 * @author www
 * @date 2016年1月24日
 */

public class SqlMapDaoTransactionManager implements DaoTransactionManager {

	private SqlMapClient client;
	
	/**
	 * 配置事务管理器
	 */
	@Override
	public void configure(Properties properties) {
		try {
			Reader reader = null;
			if (properties.containsKey("SqlMapConfigURL")) {
				reader = Resources.getUrlAsReader(properties.getProperty("SqlMapConfigURL"));
			} else if (properties.containsKey("SqlMapConfigResource")) {
				reader = Resources.getResourceAsReader(properties.getProperty("SqlMapConfigResource"));
			} else {
				throw new DaoException("SQLMAP transaction manager requires either 'SqlMapConfigURL' or 'SqlMapConfigResource' to be specified as a property.");
			}
			client = SqlMapClientBuilder.buildSqlMapClient(reader, properties);
		} catch (IOException e) {
			throw new DaoException("Error configuring SQL Map. Cause: " + e);
		}
	}

	/**
	 * 开始事务
	 */
	@Override
	public DaoTransaction startTransaction() {
		return new SqlMapDaoTransaction(client);
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

}
