package com.biotronisis.pettplant.persist;

//import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import java.sql.SQLException;
import java.util.List;

public abstract class AbstractDao<T> {
	
//	@Inject
	protected Dao<T, Long> baseDao;

	public CreateOrUpdateStatus createOrUpdate(T obj) throws SQLException {
		return baseDao.createOrUpdate(obj);
	}

	public int delete(T obj) throws SQLException {
		return baseDao.delete(obj);
	}

	public List<T> queryForAll() throws SQLException {
		return baseDao.queryForAll();
	}

	public T queryForId(Long obj) throws SQLException {
		return baseDao.queryForId(obj);
	}

	public int update(T obj) throws SQLException {
		return baseDao.update(obj);
	}
	
	public int refresh(T obj) throws SQLException {
		return baseDao.refresh(obj);
	}
	
	public interface TransactionCallback {
		public void onSuccess();
		public void onFailed(Exception e);
	}
}
