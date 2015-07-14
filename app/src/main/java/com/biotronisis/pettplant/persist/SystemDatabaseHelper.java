package com.biotronisis.pettplant.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.biotronisis.pettplant.type.ColorMode;
import com.biotronisis.pettplant.type.CommunicationType;
import com.biotronisis.pettplant.type.EntrainmentMode;

import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;

import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.model.PettPlantParams;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class SystemDatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String TAG = "SystemDatabaseHelper";
	
    // name of the database file for your application
	private static final String DATABASE_NAME = "systemDatabase.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;
	
	// we do this so there is only one helper
	private static SystemDatabaseHelper instance = null;
	private static final AtomicInteger usageCounter = new AtomicInteger(0);
	
	public SystemDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		onUpgrade(db, 0, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			if (oldVersion < 1) {
				// create tables for v1
            TableUtils.createTableIfNotExists(connectionSource, CommunicationParams.class);
            TableUtils.createTableIfNotExists(connectionSource, PettPlantParams.class);


            // Pett Plant Params
            PettPlantParams pettPlantParams = new PettPlantParams();
            pettPlantParams.setDefaultUse(true);
            pettPlantParams.setColorMode(ColorMode.SOUND_RESPONSIVE);
            pettPlantParams.setColorModeSpeed(50);
            pettPlantParams.setColorModeRunButton("Off");
            pettPlantParams.setColorModePauseButton("Pause");

            pettPlantParams.setEntrainmentSequence(EntrainmentMode.MEDITATE);
            pettPlantParams.setEntrainmentLoopCheckbox("Off");
            pettPlantParams.setEntrainmentRunButton("Stop");
            pettPlantParams.setEntrainmentPauseButton("Pause");

            Dao<PettPlantParams, Long> pettPlantParamsDao = getDao(PettPlantParams.class);
            pettPlantParamsDao.create(pettPlantParams);

                // Communication Params
				CommunicationParams communicationParams = new CommunicationParams();
				communicationParams.setDefaultUse(true);
				communicationParams.setName("Mock ZLS Device");
				communicationParams.setCommunicationType(CommunicationType.MOCK);
				communicationParams.setAddress("00:00:00:00:00:00");
				
				Dao<CommunicationParams, Long> communicationParamsDao = getDao(CommunicationParams.class);
				communicationParamsDao.create(communicationParams);
			}
		} catch (SQLException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Can't create database", e);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.SEVERE, "SystemDatabaseHelper.onUpgrade(): " +
            		"Can't create database - " + e, 
            		0, 0);
		}
	}
	
	/**
	 * Get the helper, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
	 * call to {@link #close()}.
	 */
	public static synchronized SystemDatabaseHelper getHelper(Context context) {
		if (instance == null) {
			instance = new SystemDatabaseHelper(context);
		}
		usageCounter.incrementAndGet();
		return instance;
	}
	
	/**
	 * Close the database connections and clear any cached DAOs. For each call to {@link #getHelper(Context)}, there
	 * should be 1 and only 1 call to this method. If there were 3 calls to {@link #getHelper(Context)} then on the 3rd
	 * call to this method, the helper and the underlying database connections will be closed.
	 */
	@Override
	public void close() {
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
			instance = null;
		}
	}
}
