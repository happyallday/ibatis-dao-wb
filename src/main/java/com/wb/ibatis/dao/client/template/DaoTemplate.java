package com.wb.ibatis.dao.client.template;

import com.wb.ibatis.dao.client.Dao;
import com.wb.ibatis.dao.client.DaoManager;

/**
 * @author www
 * @date 2016年1月9日
 * 
 * 实现Dao接口，所有Dao模板类的基类，框架的使用者(Dao实例)需继承这些模板类。
 * 
 */

public abstract class DaoTemplate implements Dao {

	protected DaoManager daoManager;

	/**
	 * 使用这个Dao实例的管理者DaoManager做参数来实例化
	 * @param daoManager
	 */
	public DaoTemplate(DaoManager daoManager) {
		this.daoManager = daoManager;
	}
	
}
