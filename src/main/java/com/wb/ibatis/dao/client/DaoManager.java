package com.wb.ibatis.dao.client;

/**
 * @author www
 * @date 2016年1月9日
 * 
 * 定义了DAO的管理者接口，提供访问所有它管理的DAO，以及事务的提交与结束(如回滚)的方法。
 */

public interface DaoManager {

	/**
	 * 根据type类型获取一个DAO实例。
	 * @param type 需要返回的DAO实例的接口类型。
	 * @return
	 */
	public Dao getDao(Class<?> type);
	
	/**
	 * 从指定的context中根据type类型获取一个DAO实例。
	 * @param type 需要返回的DAO实例的接口类型。
	 * @param contextId 指定context的contextId，从这个context中找到需返回DAO实例。
	 * @return
	 */
	public Dao getDao(Class<?> type, String contextId);
	
	
	/**
	 * 获取指定的Dao对象所在的事务对象，如果Dao对象没在事务中，则必会启动一个事务。
	 * @param dao
	 * @return
	 */
	public DaoTransaction getTransaction(Dao dao);
	
	/**
	 * 开始这个DaoManager所管理的事务，
	 * 如果这个方法没被调用，所有Dao方法会使用"autocommit"。
	 */
	public void startTransaction();
	
	/**
	 * 提交这个DaoManager所管理的所有Dao Context中正在被started的事务。
	 */
	public void commitTransaction();
	
	/**
	 * 结束这个DaoManager所管理的所有Dao Context中正在被started的事务，
	 * 如果事务还没有成功提交，则会被回滚。
	 */
	public void endTransaction();
	
	
}
