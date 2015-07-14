package com.biotronisis.pettplant;

import android.content.Context;
import android.util.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import com.j256.ormlite.dao.Dao;

import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;

import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.model.PettPlantParams;

import com.biotronisis.pettplant.persist.CommunicationParamsDao;
import com.biotronisis.pettplant.persist.PettPlantParamsDao;

import com.biotronisis.pettplant.persist.SystemDatabaseHelper;

import java.sql.SQLException;
import java.util.logging.Level;

public class MyModule extends AbstractModule {

    private static final String TAG = "MyModule";

    private final Context context;
    private static ErrorHandler errorHandler;

    public MyModule(Context context) {
        this.context = context;
        if (MyDebug.LOG) {
            Log.d(TAG, "MyModule constructor");
        }
        errorHandler = ErrorHandler.getInstance();
        errorHandler.logError(Level.INFO, "MyModule.MyModule()", 0, 0);
    }

    /**
     * This configures the applications class to impl mappings for injected
     * things.
     */
    @Override
    protected void configure() {
        
        if (MyDebug.LOG) {
            Log.d(TAG, "Entered configure()");
        }

        bind(SystemDatabaseHelper.class).toProvider(new SystemDatabaseHelperProvider(context));

       bind(PettPlantParamsDao.class);
       bind(new TypeLiteral<Dao<CommunicationParams, Long>>() {
       }).toProvider(CommunicationParamsBaseDaoProvider.class);

       bind(CommunicationParamsDao.class);
       bind(new TypeLiteral<Dao<CommunicationParams, Long>>() {
       }).toProvider(CommunicationParamsBaseDaoProvider.class);
    }

    private static class SystemDatabaseHelperProvider implements Provider<SystemDatabaseHelper> {

        private Context context;

        public SystemDatabaseHelperProvider(Context context) {
            this.context = context;
        }

        @Override
        public SystemDatabaseHelper get() {
            return SystemDatabaseHelper.getHelper(context);
        }
    }


   private static class PettPlantParamsBaseDaoProvider implements
         Provider<Dao<CommunicationParams, Long>> {

      @Inject
      private SystemDatabaseHelper systemDatabaseHelper;

      @Override
      public Dao<PettPlantParams, Long> get() {

         Dao<PettPlantParams, Long> result = null;

         try {
            result = systemDatabaseHelper.getDao(PettPlantParams.class);
         } catch (SQLException e) {
            if (MyDebug.LOG) {
               Log.e(TAG, "Unable to create PettPlantParamsBaseDao", e);
            }
            errorHandler.logError(Level.SEVERE, "MyModule$PettPlantParamsBaseDaoProvider.get(): " +
                  "Unable to create PettPlantParamsBaseDaoProvider - " + e, 0, 0);
         }

         return result;
      }
   }

   private static class CommunicationParamsBaseDaoProvider implements
         Provider<Dao<CommunicationParams, Long>> {

      @Inject
      private SystemDatabaseHelper systemDatabaseHelper;

      @Override
      public Dao<CommunicationParams, Long> get() {

         Dao<CommunicationParams, Long> result = null;

         try {
            result = systemDatabaseHelper.getDao(CommunicationParams.class);
         } catch (SQLException e) {
            if (MyDebug.LOG) {
               Log.e(TAG, "Unable to create CommunicationParamsBaseDao", e);
            }
            errorHandler.logError(Level.SEVERE, "MyModule$CommunicationParamsBaseDaoProvider.get(): " +
                  "Unable to create CommunicationParamsBaseDaoProvider - " + e, 0, 0);
         }

         return result;
      }
   }

}
