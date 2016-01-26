package com.wb.ibatis.dao.engine.impl;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * 事务的状态，共有四种状态
 * 
 */

public class DaoTransactionState {

	public static final DaoTransactionState ACTIVE = new DaoTransactionState(); // 活着
	public static final DaoTransactionState INACTIVE = new DaoTransactionState(); // 死了
	public static final DaoTransactionState COMMITED = new DaoTransactionState(); // 已提交
	public static final DaoTransactionState ROLLEDBACK = new DaoTransactionState(); // 已回滚
	
	private DaoTransactionState() {
		
	}
}
