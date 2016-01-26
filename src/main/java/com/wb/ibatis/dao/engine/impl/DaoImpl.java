package com.wb.ibatis.dao.engine.impl;

import com.wb.ibatis.dao.client.Dao;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * DAO的各种信息，包括接口、实现类、实例对象、代理对象
 * 
 */

public class DaoImpl {

	private StandardDaoManager daoManager;
	private DaoContext daoContext;
	private Class<?> daoInterface; // dao接口
	private Class<? extends Dao> daoImplementation; // dao实现类
	private Dao daoInstance; // dao实现类的实例对象
	private Dao proxy; // dao接口的代理对象
	
	public StandardDaoManager getDaoManager() {
		return daoManager;
	}
	
	public void setDaoManager(StandardDaoManager daoManager) {
		this.daoManager = daoManager;
	}
	
	public DaoContext getDaoContext() {
		return daoContext;
	}
	
	public void setDaoContext(DaoContext daoContext) {
		this.daoContext = daoContext;
	}
	
	public Class<?> getDaoInterface() {
		return daoInterface;
	}
	
	public void setDaoInterface(Class<?> daoInterface) {
		this.daoInterface = daoInterface;
	}
	
	public Class<? extends Dao> getDaoImplementation() {
		return daoImplementation;
	}
	
	public void setDaoImplementation(Class<? extends Dao> daoImplementation) {
		this.daoImplementation = daoImplementation;
	}
	
	public Dao getDaoInstance() {
		return daoInstance;
	}
	
	public void setDaoInstance(Dao daoInstance) {
		this.daoInstance = daoInstance;
	}
	
	public Dao getProxy() {
		return proxy;
	} 
	
	public void initProxy() {
		this.proxy = DaoProxy.newInstance(this);
	}
	
}
