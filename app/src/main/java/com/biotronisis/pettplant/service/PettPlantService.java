package com.biotronisis.pettplant.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.biotronisis.pettplant.persist.CommunicationParamsDao;
import com.biotronisis.pettplant.communication.CommunicationManager;
import com.biotronisis.pettplant.communication.CommunicationManager.CommunicationManagerListener;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.model.CommunicationParams;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by john on 7/10/15.
 */
public class PettPlantService extends Service {

   private static final String TAG = "PettPlantService";

   private static final int NOTIFICATION_ID = 0xAA00001;

   public static final String PETT_PLANT_SERVICE_EVENT = "pett plant service event";
   public static final String PETT_PLANT_SERVICE_CREATED = "pett plant service created";
   public static final String PETT_PLANT_SERVICE_DESTROYED = "pett plant service destroyed";

   private static PettPlantService instance;

   private NotificationManager notificationManager;

   private CommunicationParamsDao communicationParamsDao;

   // registry of listeners that want to be notified for the status of communications *access
   // synchronized on statusListeners*
   public Set<CommunicationManagerListener> statusListeners =
         new LinkedHashSet<CommunicationManagerListener>();

   private boolean readingInProgress = false;

   private CommunicationManager communicationManager;

   private Handler uiHandler;

   public static Intent createIntent(Context callee) {
      Intent intent = new Intent(callee, PettPlantService.class);
      return intent;
   }

   @Override
   public void onCreate() {
      super.onCreate();

      // save the ref to the singleton managed by android
      instance = this;

//      ErrorHandler errorHandler = ErrorHandler.getInstance();
//      if (errorHandler == null) {
//         if (MyDebug.LOG) {
//            Log.d(TAG, "MeterService.onCreate() - errorHandler is null");
//         }
//      } else {
//         errorHandler.logError(Level.INFO, "MeterService.onCreate().", 0, 0);
//      }
      if (MyDebug.LOG) {
         Log.d(TAG, "PettPlantService.onCreate() - Entered");
      }

      uiHandler = new Handler(getMainLooper());

      try {
         CommunicationParams communicationParams = communicationParamsDao.queryForDefault();
         communicationManager = new CommunicationManager(this, communicationParams);
         communicationManager.connect();
      } catch (SQLException e) {
         if (MyDebug.LOG) {
            Log.e(TAG, "failed to retrieve default CommunicationsParams or a valid commAdapter " +
                  "was not created", e);
         }
//         errorHandler.logError(Level.SEVERE, "MeterService.onCreate(): " +
//                     "Failed to retrieve default CommunicationsParams - " + e,
//               R.string.communication_params_file_open_error_title,
//               R.string.communication_params_file_open_error_message);
      }

      IntentFilter usbFilter = new IntentFilter();
      usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
      usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
      usbFilter.setPriority(500);
      this.registerReceiver(mUsbReceiver, usbFilter);

      Intent intent = new Intent(PETT_PLANT_SERVICE_EVENT);
      intent.putExtra("message", PETT_PLANT_SERVICE_CREATED);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
   }
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
}
