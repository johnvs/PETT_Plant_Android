package com.biotronisis.pettplant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.MainActivity;
import com.biotronisis.pettplant.communication.CommunicationErrorType;
import com.biotronisis.pettplant.communication.ConnectionState;
import com.biotronisis.pettplant.communication.ICommAdapter;
import com.biotronisis.pettplant.communication.transfer.EmptyResponse;
import com.biotronisis.pettplant.communication.transfer.PauseEntrainmentCommand;
import com.biotronisis.pettplant.communication.transfer.ResponseCallback;
import com.biotronisis.pettplant.communication.transfer.ResumeEntrainmentCommand;
import com.biotronisis.pettplant.communication.transfer.RunEntrainmentCommand;
import com.biotronisis.pettplant.communication.transfer.StopEntrainmentCommand;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.communication.CommunicationManager;
import com.biotronisis.pettplant.communication.CommunicationManager.CommunicationManagerListener;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.type.EntrainmentMode;

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

//   private CommunicationParamsDao communicationParamsDao;

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

      notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

      ErrorHandler errorHandler = ErrorHandler.getInstance();
      if (errorHandler == null) {
         if (MyDebug.LOG) {
            Log.d(TAG, "PettPlantService.onCreate() - errorHandler is null");
         }
      } else {
         errorHandler.logError(Level.INFO, "PettPlantService.onCreate().", 0, 0);
      }
      if (MyDebug.LOG) {
         Log.d(TAG, "PettPlantService.onCreate() - Entered");
      }

      uiHandler = new Handler(getMainLooper());

//      try {
      CommunicationParams communicationParams = new CommunicationParams(this);

//         CommunicationParams communicationParams = communicationParamsDao.queryForDefault();
//         communicationManager = new CommunicationManager(this, communicationParams);
      communicationManager = new CommunicationManager(this, communicationParams);
      communicationManager.connect();
//      } catch (SQLException e) {
//         if (MyDebug.LOG) {
//            Log.e(TAG, "failed to retrieve default CommunicationsParams or a valid commAdapter " +
//                  "was not created", e);
//         }
////         errorHandler.logError(Level.SEVERE, "MeterService.onCreate(): " +
////                     "Failed to retrieve default CommunicationsParams - " + e,
////               R.string.communication_params_file_open_error_title,
////               R.string.communication_params_file_open_error_message);
//      }

//      IntentFilter usbFilter = new IntentFilter();
//      usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//      usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//      usbFilter.setPriority(500);
//      this.registerReceiver(mUsbReceiver, usbFilter);

      Intent intent = new Intent(PETT_PLANT_SERVICE_EVENT);
      intent.putExtra("message", PETT_PLANT_SERVICE_CREATED);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      if (communicationManager != null) {
         communicationManager.disconnect();
      }

      instance = null;
      ErrorHandler errorHandler = ErrorHandler.getInstance();
      if (errorHandler != null) {
         errorHandler.logError(Level.INFO, "PettPlantService.onDestroy().", 0, 0);
      } else {
         if (MyDebug.LOG) {
            Log.d(TAG, "errorHandler is null.");
         }
      }

      Runnable run = new Runnable() {
         public void run() {
            notificationManager.cancel(NOTIFICATION_ID);
         }
      };
      uiHandler.post(run);

//      unregisterReceiver(mUsbReceiver);

      // Let others know that the service is being destroyed
      Intent intent = new Intent(PETT_PLANT_SERVICE_EVENT);
      intent.putExtra("message", PETT_PLANT_SERVICE_DESTROYED);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
   }

   public static PettPlantService getInstance() {
      return instance;
   }

   /**
    * Adds a listener to be notified for the status of communications
    */
   public synchronized void addCommStatusListener(CommunicationManagerListener listener) {
      if (MyDebug.LOG) {
         Log.d(TAG, "Adding CommStatusListener");
      }
      synchronized (statusListeners) {
         statusListeners.add(listener);
      }
   }

   /**
    * Removes a listener to be notified for the status of communications
    */
   public synchronized void removeCommStatusListener(CommunicationManagerListener listener) {
      boolean result;
      synchronized (statusListeners) {
         result = statusListeners.remove(listener);
      }
      if (MyDebug.LOG) {
         Log.d(TAG, "Removing CommStatusListener " + result);
      }
   }

   public boolean isReConnectingToBtDevice(String address) {
      return communicationManager.isReConnectingToBtDevice(address);
   }

   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   private Notification createNotification(String message, boolean ongoing) {
      if (MyDebug.LOG) {
         Log.d(TAG, "Create Notification with message: " + message);
      }
      Intent intent = MainActivity.createIntent(this);
      intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

      BitmapDrawable icon = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.ic_launcher);
      // depricated method getResources().getDrawable(R.drawable.ic_launcher);


      Notification notification = new NotificationCompat.Builder(this)
            .setContentTitle(getString(R.string.pett_plant))
            .setContentText(message).setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(icon.getBitmap())
            .setOngoing(ongoing)
            .setTicker(message)
            .build();
      return notification;
   }

   public boolean isConnected() {
      ICommAdapter commAdapter = null;

      if (MyDebug.LOG) {
         Log.d(TAG, "isConnected");
      }

      if (communicationManager != null) {
         commAdapter = communicationManager.getCurrentCommAdapter();
      }
      if (commAdapter == null) {
         return false;
      }
      return commAdapter.getConnectionState() == ConnectionState.ESTABLISHED;
   }

   public void onCommConnecting() {
      dispatchCommConnecting();
   }

   private void dispatchCommConnecting() {
      Runnable run = new Runnable() {
         public void run() {
            String message = getString(R.string.title_connecting);
//            notificationManager.notify(NOTIFICATION_ID, createNotification(message, true));
            Toast.makeText(PettPlantService.this, message, Toast.LENGTH_SHORT).show();

            synchronized (statusListeners) {
               for (CommunicationManagerListener listener : statusListeners) {
                  listener.onConnecting();
               }
            }
         }
      };
      uiHandler.post(run);
   }

   public void onCommConnected() {
      if (MyDebug.LOG) {
         Log.d(TAG, "onCommConnected");
      }

      dispatchCommConnected(); // This represents a simple BT connection, not any communications
                               // between the app and the plant's code.


      // send an AlternateBreak right after comm connect then dispatch connected if the command
      // was responded to
//      AlternateBreakCommand breakCommand = new AlternateBreakCommand();
//      breakCommand.setResponseCallback(new ResponseCallback<AlternateBreakResponse>() {
//         @Override
//         public void onResponse(AlternateBreakResponse response) {
//            dispatchCommConnected();
//
//            // Initialize the meter's duty cycle to 50%
//            runEntrainmentSequence(32767, new RunEntrainmentCallback() {
//
//               @Override
//               public void onSuccess() {
//                  if (MyDebug.LOG) {
//                     Log.d(TAG, "OnCommConnected runEntrainmentSequence success.");
//                  }
//               }
//
//               @Override
//               public void onFailed(String reason) {
//                  if (MyDebug.LOG) {
//                     Log.e(TAG, "OnCommConnected runEntrainmentSequence failed. " + reason);
//                  }
//
//                  ErrorHandler errorHandler = ErrorHandler.getInstance();
//                  errorHandler.logError(Level.WARNING, "MeterService.onCommConnected()$" +
//                              "onResponse$RunEntrainmentCallback.onFailed(): " +
//                              "runEntrainmentSequence() failed - " + reason,
//                        R.string.run_entrainment_failed_title,
//                        R.string.run_entrainment_failed_message);
//               }
//            });
//         }
//
//         @Override
//         public void onFailed(CommunicationErrorType type) {
//            dispatchCommError(type, toUserMessage(type), AlternateBreakCommand.COMMAND_ID);
//
//            ErrorHandler errorHandler = ErrorHandler.getInstance();
//            errorHandler.logError(Level.WARNING, "MeterService.onCommConnected()$" +
//                        "ResponseCallback.onFailed(): breakCommand failed with the error - " + type,
//                  R.string.meter_not_responding_title,
//                  R.string.meter_not_responding_message);
//         }
//      });
//      communicationManager.sendCommand(breakCommand);
   }

   private void dispatchCommConnected() {
      final CommunicationParams communicationParams = new CommunicationParams(this);

      Runnable run = new Runnable() {
         public void run() {
            String message = getString(R.string.title_connected_to, communicationParams.getName());
            notificationManager.notify(NOTIFICATION_ID, createNotification(message, true));
            Toast.makeText(PettPlantService.this, message, Toast.LENGTH_SHORT).show();

            synchronized (statusListeners) {
               for (CommunicationManagerListener listener : statusListeners) {
                  listener.onConnected();
               }
            }
         }
      };
      uiHandler.post(run);
   }

   public void onCommDisconnected() {
      dispatchCommDisconnected();
   }

   private void dispatchCommDisconnected() {
      Runnable run = new Runnable() {
         public void run() {
            String message = getString(R.string.title_not_connected);
//            notificationManager.notify(NOTIFICATION_ID, createNotification(message, false));
            Toast.makeText(PettPlantService.this, message, Toast.LENGTH_SHORT).show();

            synchronized (statusListeners) {
               for (CommunicationManagerListener listener : statusListeners) {
                  listener.onDisconnected();
               }
            }
         }
      };
      uiHandler.post(run);
   }

   public void onCommFailed() {
      dispatchCommFailed();
   }

   private void dispatchCommFailed() {
      Runnable run = new Runnable() {
         public void run() {
//                String message = getString(R.string.meter_comm_failed);
//            notificationManager.notify(NOTIFICATION_ID, createNotification(getString(
//                  R.string.meter_disconnected), false));
            Toast.makeText(PettPlantService.this, getString(R.string.title_comm_failed),
                  Toast.LENGTH_SHORT).show();
         }
      };
      uiHandler.post(run);
   }

   public void dispatchCommSent() {
      Runnable run = new Runnable() {
         public void run() {
            synchronized (statusListeners) {
               for (CommunicationManagerListener listener : statusListeners) {
                  listener.onDataSent();
               }
            }
         }
      };
      uiHandler.post(run);
   }

   public void dispatchCommRecieved() {
      Runnable run = new Runnable() {
         public void run() {
            synchronized (statusListeners) {
               for (CommunicationManagerListener listener : statusListeners) {
                  listener.onDataRecieved();
               }
            }
         }
      };
      uiHandler.post(run);
   }

   public void dispatchCommError(final CommunicationErrorType type, String reason, Byte commandId) {
      if (MyDebug.LOG) {
         Log.e(TAG, "dispatchCommError: " + reason);
      }

//      try {
//         CommunicationParams communicationParams = communicationParamsDao.queryForDefault();
//
//         if ((communicationParams.getCommunicationType() == CommunicationType.USB) &&
//               (type == CommunicationErrorType.TIMEOUT_RESPONSE) &&
//               (commandId != null) && (commandId == AlternateBreakCommand.COMMAND_ID)) {
//
//            communicationManager.disconnect();
//
//            // Display a toast if we get a timeout comm error after sending the alternate break
//            // command when using the USB connection
//            Runnable run = new Runnable() {
//               public void run() {
//                  notificationManager.notify(NOTIFICATION_ID, createNotification(getString(
//                        R.string.meter_not_responding_title), false));
//                  Toast.makeText(PettPlantService.this, getString(
//                        R.string.meter_not_responding_message), Toast.LENGTH_LONG).show();
//               }
//            };
//            uiHandler.post(run);
//         } else if (type == CommunicationErrorType.UNEXPECTED_RESPONSE &&
//               commandId != null && commandId == CurrentReadingResponse.RESPONSE_ID) {
//            // If the message is an obs dataset, send an end interval reading command
//            endReading(new MyEndReadingCallback());
//
//            if (MyDebug.LOG) {
//               Log.i(TAG, "dispatchCommError: handling unexpected response");
//               Log.d("EndObsRaceCond", "MeterService:dispatchCommError - Sending End Reading " +
//                     "command to meter");
//            }
//         } else {
//            Runnable run = new Runnable() {
//               public void run() {
//                  synchronized (statusListeners) {
//                     for (CommunicationManagerListener listener : statusListeners) {
//                        listener.onError(type);
//                     }
//                  }
//               }
//            };
//            uiHandler.post(run);
//         }
//      } catch (SQLException e) {
//         if (MyDebug.LOG) {
//            Log.e(TAG, "failed to retrieve default CommunicationsParams", e);
//         }
//         ErrorHandler errorHandler = ErrorHandler.getInstance();
//         errorHandler.logError(Level.WARNING, "MeterService.dispatchCommError(): " +
//                     "Failed to retrieve default CommunicationsParams - " + e,
//               R.string.communication_params_file_open_error_title,
//               R.string.communication_params_file_open_error_message);
////            stopSelf();
//      }
   }

   private String toUserMessage(CommunicationErrorType errorType) {

      switch (errorType) {
         case TIMEOUT_RESPONSE:
            return getString(R.string.plantCommandTimeout);
         case MALFORMED_RESPONSE:
            return getString(R.string.malformed_response);
         case TRANSMIT_FAILED:
            return getString(R.string.transmit_failed);
         case UNEXPECTED_RESPONSE:
            return getString(R.string.unexpected_response);
         case UNKNOWN_RESPONSE:
            return getString(R.string.unknown_response);
         default:
            return getString(R.string.plantCommandUnknownError);
      }
   }

   public void runEntrainmentSequence(EntrainmentMode eMode, RunEntrainmentCallback callback) {
      try {
         RunEntrainmentCommand command = new RunEntrainmentCommand();
         command.setEntrainmentSequence(eMode);
         command.setResponseCallback(new RunEntrainmentResponseCallback(callback));

         // Test
         boolean test = false;
         if (test) {
            throw new Exception("test");
         }

         communicationManager.sendCommand(command);
      } catch (Exception ex) {
         if (MyDebug.LOG) {
            Log.e(TAG, "failed to send run entrainment sequence", ex);
         }
         callback.onFailed(ex.toString());
      }
   }

   public void stopEntrainmentSequence(StopEntrainmentCallback callback) {
      try {
         StopEntrainmentCommand command = new StopEntrainmentCommand();
         command.setResponseCallback(new StopEntrainmentResponseCallback(callback));

         // Test
         boolean test = false;
         if (test) {
            throw new Exception("test");
         }

         communicationManager.sendCommand(command);
      } catch (Exception ex) {
         if (MyDebug.LOG) {
            Log.e(TAG, "failed to send stop entrainment sequence", ex);
         }
         callback.onFailed(ex.toString());
      }
   }

   public void pauseEntrainmentSequence(PauseEntrainmentCallback callback) {
      try {
         PauseEntrainmentCommand command = new PauseEntrainmentCommand();
         command.setResponseCallback(new PauseEntrainmentResponseCallback(callback));

         // Test
         boolean test = false;
         if (test) {
            throw new Exception("test");
         }

         communicationManager.sendCommand(command);
      } catch (Exception ex) {
         if (MyDebug.LOG) {
            Log.e(TAG, "failed to send stop entrainment sequence", ex);
         }
         callback.onFailed(ex.toString());
      }
   }

   public void resumeEntrainmentSequence(ResumeEntrainmentCallback callback) {
      try {
         ResumeEntrainmentCommand command = new ResumeEntrainmentCommand();
         command.setResponseCallback(new ResumeEntrainmentResponseCallback(callback));

         // Test
         boolean test = false;
         if (test) {
            throw new Exception("test");
         }

         communicationManager.sendCommand(command);
      } catch (Exception ex) {
         if (MyDebug.LOG) {
            Log.e(TAG, "failed to send stop entrainment sequence", ex);
         }
         callback.onFailed(ex.toString());
      }
   }

   private class RunEntrainmentResponseCallback implements ResponseCallback<EmptyResponse> {

      private RunEntrainmentCallback callback;

      public RunEntrainmentResponseCallback(RunEntrainmentCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class StopEntrainmentResponseCallback implements ResponseCallback<EmptyResponse> {

      private StopEntrainmentCallback callback;

      public StopEntrainmentResponseCallback(StopEntrainmentCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class PauseEntrainmentResponseCallback implements ResponseCallback<EmptyResponse> {

      private PauseEntrainmentCallback callback;

      public PauseEntrainmentResponseCallback(PauseEntrainmentCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class ResumeEntrainmentResponseCallback implements ResponseCallback<EmptyResponse> {

      private ResumeEntrainmentCallback callback;

      public ResumeEntrainmentResponseCallback(ResumeEntrainmentCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class SetColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private SetColorModeCallback callback;

      public SetColorModeResponseCallback(SetColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class RunColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private RunColorModeCallback callback;

      public RunColorModeResponseCallback(RunColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class OffColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private OffColorModeCallback callback;

      public OffColorModeResponseCallback(OffColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class PauseColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private PauseColorModeCallback callback;

      public PauseColorModeResponseCallback(PauseColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class ResumeColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private ResumeColorModeCallback callback;

      public ResumeColorModeResponseCallback(ResumeColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   private class SetSpeedColorModeResponseCallback implements ResponseCallback<EmptyResponse> {

      private SetSpeedColorModeCallback callback;

      public SetSpeedColorModeResponseCallback(SetSpeedColorModeCallback callback) {
         this.callback = callback;
      }

      public void onResponse(EmptyResponse response) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onSuccess();
            }
         });
      }

      public void onFailed(final CommunicationErrorType type) {
         uiHandler.post(new Runnable() {
            public void run() {
               callback.onFailed(toUserMessage(type));
            }
         });
      }
   }

   public interface RunEntrainmentCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface StopEntrainmentCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface PauseEntrainmentCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface ResumeEntrainmentCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface LoopEntrainmentCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface SetColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface RunColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface OffColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface PauseColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface ResumeColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

   public interface SetSpeedColorModeCallback {

      public void onSuccess();

      public void onFailed(String reason);
   }

}
