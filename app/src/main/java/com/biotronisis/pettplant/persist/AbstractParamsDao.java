package com.biotronisis.pettplant.persist;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.Where;
//import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.AbstractParamsObject;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public abstract class AbstractParamsDao<T extends AbstractParamsObject> extends AbstractDao<T> {

	public T queryForDefault() throws SQLException {

	    Where<T, Long> query = baseDao.queryBuilder().where().eq(AbstractParamsObject.DEFAULT_USE_COLUMN, Boolean.TRUE);
        return baseDao.queryForFirst(query.prepare());
	}
	
	public void updateDefault(final T object, final TransactionCallback callback) {
		
		try {
            // Test
            boolean isTest = false;
            if (isTest) {
                throw new SQLException("SQLException test");
            }

            TransactionManager.callInTransaction(baseDao.getConnectionSource(), new Callable<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {

						try {
							// Test
							boolean isTest = false;
							if (isTest) {
								throw new SQLException("SQLException test");
							}

							// ensure on other defaults
							PreparedUpdate<T> noDefault = (PreparedUpdate<T>) baseDao.updateBuilder()
									.updateColumnValue(AbstractParamsObject.DEFAULT_USE_COLUMN, false)
									.where()
									.eq(AbstractParamsObject.DEFAULT_USE_COLUMN, true)
									.prepare();
							baseDao.update(noDefault);

							// set the new default and update
							object.setDefaultUse(true);
							baseDao.update(object);

							if (callback != null) {
								callback.onSuccess();
							}
						} catch (SQLException e) {
							if (callback != null) {
								callback.onFailed(e);
							} else {
//								ErrorHandler errorHandler = ErrorHandler.getInstance();
//								errorHandler.logError(Level.SEVERE, this.getClass().getSimpleName() +
//												".updateDefault(): Inner SQLException and callback is null - " +
//												e, 0, 0);
							}
						}
						return null;
					}
				});
		} catch (SQLException e) {
			if (callback != null) {
				callback.onFailed(e);
            } else {
//                ErrorHandler errorHandler = ErrorHandler.getInstance();
//                errorHandler.logError(Level.SEVERE, this.getClass().getSimpleName() +
//                        ".updateDefault(): Outer SQLException and callback is null - " +
//							   e, 0, 0);
			}
		}
	}
}
