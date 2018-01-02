package com.biotronisis.pettplant;

import java.util.logging.Level;

import android.app.Application;
import android.util.Log;

import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;

public class MyApplication extends Application {
    
    private static final String TAG = "MyApplication";
	
	@Override
	public void onCreate() {
	    
		super.onCreate();
		
        if (MyDebug.LOG) {
            Log.d(TAG, "Starting ErrorHandler");
        }

        ErrorHandler errorHandler = ErrorHandler.getInstance(this);
        errorHandler.logError(Level.INFO, "MyApplication.onCreate() - Starting application.", 0, 0);
	}
}
