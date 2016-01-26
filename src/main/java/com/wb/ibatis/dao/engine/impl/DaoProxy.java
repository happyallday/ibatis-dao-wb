package com.wb.ibatis.dao.engine.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import com.ibatis.common.beans.ClassInfo;
import com.wb.ibatis.dao.client.Dao;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * DAO代理类，当调用DAO实例的方法时，真正运行的都是代理类的代码。
 * 
 */

public class DaoProxy implements InvocationHandler {

	private static final Set<String> PASSTHROUGH_METHODS = new HashSet<>(); // 不用代理的方法
	
	private DaoImpl daoImpl; // 被代理的DAO实例
	
	static {
		PASSTHROUGH_METHODS.add("equales");
		PASSTHROUGH_METHODS.add("getClass");
		PASSTHROUGH_METHODS.add("hashCode");
		PASSTHROUGH_METHODS.add("notify");
		PASSTHROUGH_METHODS.add("notifyAll");
		PASSTHROUGH_METHODS.add("toString"); 
		PASSTHROUGH_METHODS.add("wait"); 
	}
	
	public DaoProxy(DaoImpl daoImpl) {
		this.daoImpl = daoImpl;
	}


	/**
	 * 进行方法的拦截，如果已经启动了事务，那就调用操作的方法。
	 * 如果没有启动事务，那就启动事务，调用操作的方法，提交事务，结束事务。
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		if (PASSTHROUGH_METHODS.contains(method.getName())) {
			try {
				result = method.invoke(daoImpl.getDaoInstance(), args);
			} catch (Throwable t) {
				throw ClassInfo.unwrapThrowable(t);
			}
		} else {
			StandardDaoManager daoManager = daoImpl.getDaoManager();
			DaoContext context = daoImpl.getDaoContext();
			

			// daoManager已经开启事务，所以需要开启事务运行，但是不提交，等daoManager提交事务时再提交(事务归高层控制)
			if (daoManager.isExplicitTransaction()) {
				try {
					context.startTransaction();
					result = method.invoke(daoImpl.getDaoInstance(), args);
				} catch (Throwable t) {
					throw ClassInfo.unwrapThrowable(t);
				}
			} else { // daoManager未开启事务，所以需要自己开启事务，提交、结束事务(事务归自己控制)
				try {
					context.startTransaction();
					result = method.invoke(daoImpl.getDaoInstance(), args);
					context.commitTransaction();
				} catch (Throwable t) {
					throw ClassInfo.unwrapThrowable(t);
				} finally {
					context.endTransaction();
				}
			}
		}
		return result;
	}

	public static Dao newInstance(DaoImpl daoImpl) {
		return (Dao) Proxy.newProxyInstance(daoImpl.getDaoInterface().getClassLoader(), 
				new Class<?>[] {Dao.class, daoImpl.getDaoInterface()}, 
				new DaoProxy(daoImpl));
	}
}






















