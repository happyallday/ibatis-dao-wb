package com.wb.ibatis.dao.client;

import java.io.Reader;
import java.util.Properties;

/**
 * @author www
 * @date 2016年1月9日
 * 
 * 根据Reader(dao.xml)和properties来生成一个DaoManager实例。
 */

public class DaoManagerBuilder {

	/**
	 * 该类只是个工具类，没有实例。
	 */
	private DaoManagerBuilder() {
		
	}
	
	/**
	 * 根绝Reader(dao.xml)来生成一个Dao Manager。
	 * @param reader 一个Reader实例，用来读取DAO框架的配置文件(dao.xml)。
	 * @param props DAO框架配置文件(dao.xml)中可能会有的一些键值对参数。
	 * @return
	 * @throws DaoException
	 */
	public static DaoManager buildDaoManager(Reader reader, Properties props)
		throws DaoException {
		
	}
	
	/**
	 * 根绝Reader(dao.xml)来生成一个Dao Manager。
	 * @param reader 一个Reader实例，用来读取DAO框架的配置文件(dao.xml)。
	 * @return
	 */
	public static DaoManager buildDaoManager(Reader reader) {
		
	}
}
