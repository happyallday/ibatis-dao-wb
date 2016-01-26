package com.wb.ibatis.dao.engine.transaction;

import java.util.Properties;

import com.wb.ibatis.dao.client.DaoTransaction;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * 事务管理器接口
 * 
 */

public interface DaoTransactionManager {

	/**
	 * 配置事务管理器
	 */
	public void configure(Properties properties);
	
	/**
	 * 开始事务
	 */
	public DaoTransaction startTransaction();
	
	/**
	 * 提交事务
	 */
	public void commitTransaction(DaoTransaction transaction);
	
	/**
	 * 回滚事务
	 */
	public void rollbackTransaction(DaoTransaction transaction);
}
