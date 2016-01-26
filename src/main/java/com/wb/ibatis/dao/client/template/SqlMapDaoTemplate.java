package com.wb.ibatis.dao.client.template;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapTransactionManager;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoManager;
import com.wb.ibatis.dao.engine.transaction.sqlmap.SqlMapDaoTransaction;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * SQL MAP模板类
 * 提供一个方便的方法来访问SqlMapExecutor。
 * 并提供SqlMapExecutor接口方法的包装器，将SQLExceptions转换为DAO Exceptions。
 * 
 */

public class SqlMapDaoTemplate extends DaoTemplate implements SqlMapExecutor {

	/**
	 * DaoManager在初始化时传入
	 * 
	 * @param daoManager
	 */
	public SqlMapDaoTemplate(DaoManager daoManager) {
		super(daoManager);
	}
	
	/**
	 * 返回本Dao所在的DaoTransaction相关的SQL Map Executor。
	 * SqlMapExecutor接口定义了一些执行方法，这里返回一个SqlMapClient实例。
	 * 
	 * @return 一个SqlMapExecutor接口的实例
	 */
	protected SqlMapExecutor getSqlMapExecutor() {
		SqlMapDaoTransaction transaction = (SqlMapDaoTransaction) daoManager.getTransaction(this);
		return transaction.getSqlMap();
	}
	
	/**
	 * 返回本Dao所在的DaoTransaction相关的SQL Map Transaction Manager。
	 * SqlMapTransactionManager接口定义了一些执行方法，这里返回一个SqlMapClient实例。
	 * 
	 * @return 一个SqlMapTransactionManager接口的实例
	 */
	protected SqlMapTransactionManager getSqlMapTransactionManager() {
		SqlMapDaoTransaction transaction = (SqlMapDaoTransaction) daoManager.getTransaction(this);
		return transaction.getSqlMap();
	}

	/**
	 * 执行一个映射的SQL插入语句
	 * insert和update方法有所不同，因为insert操作会返回新插入行的主键，这个功能是可选的。
	 * 返回对象为新插入行在数据库中的主键，这个主键值也会填充在parameterObject对应的字段中
	 * 
	 * @return 新插入行在数据库中的主键
	 */
	@Override
	public Object insert(String id, Object parameterObject) {
		try {
			return getSqlMapExecutor().insert(id, parameterObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to insert - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL插入语句
	 * insert和update方法有所不同，因为insert操作会返回新插入行的主键，这个功能是可选的。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 新插入行在数据库中的主键
	 */
	@Override
	public Object insert(String id) {
		try {
			return getSqlMapExecutor().insert(id);
		} catch (SQLException e) {
			throw new DaoException("Failed to insert - id [" + 
					id + "] Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL更新语句
	 * update方法也可以用insert和delete操作，返回数据库中被改变的行数。
	 * 
	 * @return 数据库中被改变的行数
	 */
	@Override
	public int update(String id, Object parameterObject) {
		try {
			return getSqlMapExecutor().update(id, parameterObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to update - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL更新语句
	 * update方法也可以用insert和delete操作，返回数据库中被改变的行数。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 数据库中被改变的行数
	 */
	@Override
	public int update(String id) {
		try {
			return getSqlMapExecutor().update(id);
		} catch (SQLException e) {
			throw new DaoException("Failed to update - id [" + 
					id + "] Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL删除语句
	 * delete方法返回数据库中被改变的行数。
	 * 
	 * @return 数据库中被改变的行数
	 */
	@Override
	public int delete(String id, Object parameterObject) {
		try {
			return getSqlMapExecutor().delete(id, parameterObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to delete - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL删除语句
	 * delete方法返回数据库中被改变的行数。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 数据库中被改变的行数
	 */
	@Override
	public int delete(String id) {
		try {
			return getSqlMapExecutor().delete(id);
		} catch (SQLException e) {
			throw new DaoException("Failed to delete - id [" + 
					id + "] Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装在一个对象中并返回。
	 * 
	 * @return 封装了查询结果的一个对象
	 */
	@Override
	public Object queryForObject(String id, Object parameterObject) {
		try {
			return getSqlMapExecutor().queryForObject(id, parameterObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForObject - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装在一个对象中并返回。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 封装了查询结果的一个对象
	 */
	@Override
	public Object queryForObject(String id) {
		try {
			return getSqlMapExecutor().queryForObject(id);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForObject - id [" + 
					id + "] Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装在一个对象中并返回。
	 * 
	 * @return 将查询结果封装在resultObject对象中并返回
	 */
	@Override
	public Object queryForObject(String id, Object parameterObject,
			Object resultObject) {
		try {
			return getSqlMapExecutor().queryForObject(id, parameterObject, resultObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForObject - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表并返回。
	 * 
	 * @return 查询结果对象列表
	 */
	@Override
	public List queryForList(String id, Object parameterObject) {
		try {
			return getSqlMapExecutor().queryForList(id, parameterObject);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForList - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表并返回。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 查询结果对象列表
	 */
	@Override
	public List queryForList(String id) {
		try {
			return getSqlMapExecutor().queryForList(id);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForList - id [" + 
					id + "] Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表，并返回指定范围中的数据。
	 * 
	 * @return 查询结果对象列表
	 */
	@Override
	public List queryForList(String id, Object parameterObject, int skip, int max) {
		try {
			return getSqlMapExecutor().queryForList(id, parameterObject, skip, max);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForList - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"], skip [" + skip + "], max[" + max + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表，并返回指定范围中的数据。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 查询结果对象列表
	 */
	@Override
	public List queryForList(String id, int skip, int max) {
		try {
			return getSqlMapExecutor().queryForList(id,  skip, max);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForList - id [" + 
					id + "], skip [" + skip + "], max[" + max + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果用一个RowHandler处理。
	 * 在返回结果集较大时(i.e. hundreds, thousands...)使用这个方法较好，因为消耗的系统资源少。
	 */
	@Override
	public void queryWithRowHandler(String id, Object parameterObject,
			RowHandler rowHandler) {
		try {
			getSqlMapExecutor().queryWithRowHandler(id, parameterObject, rowHandler);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryWithRowHandler - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"], rowHandler [" + rowHandler + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果用一个RowHandler处理。
	 * 在返回结果集较大时(i.e. hundreds, thousands...)使用这个方法较好，因为消耗的系统资源少。
	 * 这个重载方法假设没参数的方法是需要的
	 */
	@Override
	public void queryWithRowHandler(String id, RowHandler rowHandler) {
		try {
			getSqlMapExecutor().queryWithRowHandler(id, rowHandler);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryWithRowHandler - id [" + 
					id + "], rowHandler [" + rowHandler + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表(按分页大小)并返回。
	 * 
	 * @return 查询结果对象列表(用PaginatedList封装)
	 */
	@Override
	public PaginatedList queryForPaginatedList(String id,
			Object parameterObject, int pageSize) {
		try {
			return getSqlMapExecutor().queryForPaginatedList(id, parameterObject, pageSize);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForPaginatedList - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"], pageSize [" + pageSize + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个列表(按分页大小)并返回。
	 * 这个重载方法假设没参数的方法是需要的
	 * 
	 * @return 查询结果对象列表(用PaginatedList封装)
	 */
	@Override
	public PaginatedList queryForPaginatedList(String id, int pageSize) {
		try {
			return getSqlMapExecutor().queryForPaginatedList(id, pageSize);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForPaginatedList - id [" + 
					id + "], pageSize [" + pageSize + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个Map并返回。Map的key为keyProp属性对应的值，value为查询结果实体类。
	 * 例如：queryForMap("person.getPersonMapById", id, "name")返回
	 * {李四=Person [id=908, name=李四, sex=男, birthday=Mon May 31 01:46:40 CST 1982, address=浙江省宁波市]}
	 * 
	 * @return 查询结果Map
	 */
	@Override
	public Map queryForMap(String id, Object parameterObject, String keyProp) {
		try {
			return getSqlMapExecutor().queryForMap(id, parameterObject, keyProp);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForPaginatedList - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"], keyProp [" + keyProp + "]. Cause: " + e, e);
		}
	}

	/**
	 * 执行一个映射的SQL查询语句
	 * 这个方法将查询结果封装成一个Map并返回。Map的key为keyProp属性对应的值，value为valueProp属性对应的值。
	 * 例如：queryForMap("person.getPersonMapById", id, "name", "address")返回
	 * {李四=浙江省宁波市}
	 * 
	 * @return 查询结果Map
	 */
	@Override
	public Map queryForMap(String id, Object parameterObject, String keyProp, String valueProp) {
		try {
			return getSqlMapExecutor().queryForMap(id, parameterObject, keyProp, valueProp);
		} catch (SQLException e) {
			throw new DaoException("Failed to queryForPaginatedList - id [" + 
					id + "], parameterObject [" + parameterObject + 
					"], keyProp [" + keyProp + "], valueProp [" + valueProp + "]. Cause: " + e, e);
		}
	}

	/**
	 * startBatch会在发送到数据库执行前缓存update语句
	 * 在批量执行很多update语句时提高了性能。
	 */
	@Override
	public void startBatch() {
		try {
			getSqlMapExecutor().startBatch();
		} catch (SQLException e) {
			throw new DaoException("Failed to startBatch. Cause: " + e, e);
		}
	}

	/**
	 * 执行或刷新当前所有被批处理的语句。
	 * 
	 * @return 被执行的语句数
	 */
	@Override
	public int executeBatch() {
		try {
			return getSqlMapExecutor().executeBatch();
		} catch (SQLException e) {
			throw new DaoException("Failed to executeBatch. Cause: " + e, e);
		}
	}

	/**
	 * 执行或刷新当前所有被批处理的语句。
	 * 
	 * @return 批处理语句执行结果列表
	 */
	@Override
	public List executeBatchDetailed() {
		try {
			return getSqlMapExecutor().executeBatchDetailed();
		} catch (BatchException e) {
			throw new DaoException("Failed to executeBatchDetailed. Cause: " + e, e);
		} catch (SQLException e) {
			throw new DaoException("Failed to executeBatchDetailed. Cause: " + e, e);
		}
	}

	

}
