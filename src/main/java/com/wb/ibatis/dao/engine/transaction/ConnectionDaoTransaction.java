package com.wb.ibatis.dao.engine.transaction;

import java.sql.Connection;

import com.wb.ibatis.dao.client.DaoTransaction;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * 能获取Connection(如JDBC、iBATIS、Hibernate、JTA)的事务接口
 * 
 */

public interface ConnectionDaoTransaction extends DaoTransaction {

	public Connection getConnection();
}
