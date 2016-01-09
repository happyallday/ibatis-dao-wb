package com.wb.ibatis.dao.client;

/**
 * @author www
 * @date 2016年1月9日
 * 
 * 定义DAO框架会抛出的异常。
 */

@SuppressWarnings("serial")
public class DaoException extends RuntimeException {

	public DaoException() {
		
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}


	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
