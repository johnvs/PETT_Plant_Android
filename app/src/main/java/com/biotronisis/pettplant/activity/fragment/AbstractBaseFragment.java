package com.biotronisis.pettplant.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.AbstractBaseActivity.MyOnClickListener;
import com.biotronisis.pettplant.activity.IntentParams;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;

import java.util.logging.Level;

public abstract class AbstractBaseFragment extends Fragment implements IntentParams {

   private static final String TAG = "AbstractBaseFragment";
//    protected boolean dummyLLHasFocus;

   public abstract void setupView(View view, Bundle savedInstanceState);

   public void populateData() {}

   public void persistData() {}

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      try {
         //Test
//         boolean test = false;
//         if (test) {
//            throw new Exception("test");
//         } else {
            setupView(view, savedInstanceState);
//         }
      } catch (Exception e) {
         if (MyDebug.LOG) {
            Log.e(TAG, "setupView failed", e);
         }
         ErrorHandler errorHandler = ErrorHandler.getInstance();
         errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onViewCreated(): " +
                     "setupView failed - " + e,
               "Alert - Setup View Error",
               "There was an error setting up the view for " + this.getClass().getSimpleName(),
               okOnClickListener);
      }
   }

   MyOnClickListener okOnClickListener = new MyOnClickListener() {
      @Override
      public void onClick() {
         getActivity().finish();
      }
   };

   @Override
   public void onResume() {
      if (MyDebug.LOG) {
         Log.d(TAG, this.getClass().getSimpleName() + ".onResume");
      }
      super.onResume();

      try {
         //Test
//            if (true) {
         populateData();
//            } else {
//                throw new Exception("test");
//            }
      } catch (Exception e) {
         if (MyDebug.LOG) {
            Log.e(TAG, "populateData failed", e);
         }
         ErrorHandler errorHandler = ErrorHandler.getInstance();
         errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onResume(): " +
                     "populateData failed - " + e,
               getString(R.string.activity_fatal_setup_title),
               getString(R.string.activity_fatal_setup_message) + this.getClass().getSimpleName(),
               okOnClickListener);
      }
   }

   @Override
   public void onPause() {
      if (MyDebug.LOG) {
         Log.d(TAG, "onPause");
      }
      try {
         //Test
//            if (true) {
         persistData();
//            } else {
//                throw new Exception("test");
//            }
      } catch (Exception e) {
         if (MyDebug.LOG) {
            Log.e(TAG, "persistData failed", e);
         }
         ErrorHandler errorHandler = ErrorHandler.getInstance();
         errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onPause(): " +
                     "persistData failed - " + e,
               getString(R.string.activity_fatal_persist_title),
               getString(R.string.activity_fatal_persist_message) + this.getClass().getSimpleName(),
               okOnClickListener);
      }
      super.onPause();
   }

}
