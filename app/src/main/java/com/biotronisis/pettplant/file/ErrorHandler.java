package com.biotronisis.pettplant.file;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.AbstractBaseActivity;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.plant.PettPlantService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ErrorHandler {

    private static final String TAG = "ErrorHandler";
    private static final String NO_ALERT = "none";
//    public static final String USE_GENERIC_MSG = "generic";

//    public static final String ERROR_HANDLING_SERVICE_EVENT = "pett plant service event";
//    public static final String ERROR_HANDLING_SERVICE_CREATED = "pett plant service created";
//    public static final String ERROR_HANDLING_SERVICE_DESTROYED = "pett plant service destroyed";

    @SuppressLint("StaticFieldLeak")
    private static ErrorHandler instance;

    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());
    private FileHandler fileHandler;
    private final Context appContext;

    public static ErrorHandler getInstance(Context callee) {
        if (instance == null) {
            instance = new ErrorHandler(callee);
        }
        return instance;
    }

    public static ErrorHandler getInstance() {
        return instance;
    }

    private ErrorHandler(Context callee) {

        if (MyDebug.LOG) {
            Log.d(TAG, "ErrorHandler onCreate");
        }
        appContext = callee.getApplicationContext();

        // save the ref to the singleton managed by android
        instance = this;

//        String state = Environment.getExternalStorageState();
        File myAppsFiles = appContext.getExternalFilesDir(null);
        try {
            if (myAppsFiles != null) {
                String path = myAppsFiles.getAbsolutePath() + "/Errors.log";
                fileHandler = new FileHandler(path, true);
                LOGGER.addHandler(fileHandler);
                fileHandler.setFormatter(new MyFormatter());
                fileHandler.setLevel(Level.ALL);
                LOGGER.setLevel(Level.ALL);
            } else {
                if (MyDebug.LOG) {
                    Log.e(TAG, "There was a problem with the SD card while trying to open the " +
                          "error log file.");
                }

                createAlertDialog(appContext.getString(R.string.sd_card_error_title),
                      appContext.getString(R.string.sd_card_error_message),
                      null);
            }
        } catch (Exception e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Could not construct a new FileHandler.");
            }
            createAlertDialog(appContext.getString(R.string.error_log_file_error_title),
                  appContext.getString(R.string.error_log_file_error_message),
                  null);
        }
    }

    private void createAlertDialog(String title, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, listener);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    class MyFormatter extends Formatter {

        // Create a DateFormat to format the logger timestamp.
        private final DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(record.getMillis()))).append(" - ");
//            builder.append("[").append(record.getSourceClassName()).append(".");
//            builder.append(record.getSourceMethodName()).append("] - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }

        public String getHead(java.util.logging.Handler h) {
            return super.getHead(h);
        }

        public String getTail(java.util.logging.Handler h) {
            return super.getTail(h);
        }
    }

    private void onDestroy() {

        instance = null;
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    /**
     * Writes an error message in the error log file
     *
     * @param level      - error severity level
     * @param logMessage - log file message
     */
    public void logError(Level level, String logMessage) {
        logError(level, logMessage, NO_ALERT, NO_ALERT, null);
    }

    /**
     * Writes an error message in the error log file and optionally displays an alert dialog
     *
     * @param level        - error severity level
     * @param logMessage   - log file message
     * @param alertTitle   - Alert dialog title.
     *                     0 = use generic error title,
     *                     "none" = do not show an alert dialog.
     * @param alertMessage - Alert dialog title message.
     *                     0 = use generic error message.
     */
    public void logError(Level level, String logMessage, Integer alertTitle, Integer alertMessage) {
        String alertTitleStr = null;
        String alertMessageStr = null;

        if (alertTitle != 0) {
            alertTitleStr = appContext.getString(alertTitle);
        }

        if (alertMessage != 0) {
            alertMessageStr = appContext.getString(alertMessage);
        }

        logError(level, logMessage, alertTitleStr, alertMessageStr);
    }

    public void logError(Level level, String logMessage, Integer alertTitle,
                         String alertMessageStr) {
        String alertTitleStr = null;

        if (alertTitle != 0) {
            alertTitleStr = appContext.getString(alertTitle);
        }

        logError(level, logMessage, alertTitleStr, alertMessageStr);
    }

    public void logError(Level level, String logMessage, String alertTitleStr,
                         String alertMessageStr) {
        logError(level, logMessage, alertTitleStr, alertMessageStr, null);
    }

    public void logError(final Level level, String logMessage, String alertTitle,
                         String alertMessage, final AbstractBaseActivity.MyOnClickListener okOnClickListener) {

        LOGGER.log(level, logMessage);

        if (alertTitle == null) {
            alertTitle = appContext.getString(R.string.generic_error_title);
        }

        if (alertMessage == null) {
            alertMessage = appContext.getString(R.string.generic_error_message);
        }

        if ((level == Level.WARNING || level == Level.SEVERE) && !alertTitle.equals(ErrorHandler.NO_ALERT)) {

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (level == Level.WARNING) {
                        dialog.dismiss();
                    } else if (level == Level.SEVERE) {
                        // Stop the pett plant service
                        boolean wasServiceStopped = appContext.stopService(PettPlantService.createIntent(appContext));
                        if (wasServiceStopped) {
                            // We just stopped the service. Listen for the broadcast of it's ultimate demise.
                            LocalBroadcastManager.getInstance(appContext).registerReceiver(pettPlantEventReceiver,
                                  new IntentFilter(PettPlantService.SERVICE_EVENT));
                        } else {
                            // The service was already stopped somewhere else, so do here what we would
                            // be doing in the receiver
                            if (instance != null) {
                                instance.logError(Level.INFO, "ErrorHandler.logError - " +
                                      "Pett plantService has been destroyed.");
                                instance.onDestroy();
                            }
                            throw new RuntimeException("An unexpected error has occurred.");
                        }
                    }
                    if (okOnClickListener != null) {
                        okOnClickListener.onClick();
                    }
                }
            };

            createAlertDialog(alertTitle, alertMessage, listener);
        }
    }

    private final BroadcastReceiver pettPlantEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(PettPlantService.EXTRA_EVENT_MESSAGE);

            if (MyDebug.LOG) {
                Log.d("receiver", "Got message: " + message);
            }

            if (message.equals(PettPlantService.SERVICE_CREATED)) {
            } else if (message.equals(PettPlantService.SERVICE_DESTROYED)) {

                // The pett plant service has stopped, after a fatal exception, so close the log file and
                // throw a RTE to kill the app.
                if (MyDebug.LOG) {
                    Log.d(TAG, "pettPlantService has been destroyed.");
                }

                if (instance != null) {
                    instance.logError(Level.INFO, "ErrorHandler.pettPlantEventReceiver - " +
                          "pettPlantService has been destroyed.");
                    instance.onDestroy();
                }
                throw new RuntimeException("An unexpected error has occurred.");
            }
        }
    };
}
