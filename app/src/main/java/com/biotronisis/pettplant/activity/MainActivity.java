package com.biotronisis.pettplant.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.fragment.PettPlantFragment;
import com.biotronisis.pettplant.communication.CommunicationErrorType;
import com.biotronisis.pettplant.communication.CommunicationManager;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.service.PettPlantService;

public class MainActivity extends AbstractBaseActivity {

   private static String TAG = "MainActivity";

//   private Context activityContext;

   private MyCommunicationManagerListener myCommunicationManagerListener =
         new MyCommunicationManagerListener();

   @Override
   public String getActivityName() {
      return TAG;
   }

   @Override
   public String getHelpKey() {
      return "main";
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Create a new Error Handler object in case the application was stopped
      // but not destroyed, in which case, MyApplication.onCreate will not be executed.
      ErrorHandler.getInstance(this.getApplicationContext());

      // During a configuration change (i.e. device rotation), the service is not stopped,
      // so check if the service is running before starting it.
      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService == null) {
         Intent intent = PettPlantService.createIntent(this);
         startService(intent);
         if (MyDebug.LOG) {
            Log.d(TAG, "Started PettPlantService.");
         }
      }

      if (savedInstanceState == null) {
         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         PettPlantFragment fragment = new PettPlantFragment();
         transaction.replace(R.id.pett_plant_fragment, fragment);
         transaction.commit();
      }

//      activityContext = this;  // Needed later for inner classes.
   }

   @Override
   public void onResume() {
      super.onResume();
      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {
         pettPlantService.addCommStatusListener(myCommunicationManagerListener);

         updateActionBar();
      }

      LocalBroadcastManager.getInstance(this).
            registerReceiver(pettPlantServiceEventReceiver,
                  new IntentFilter(PettPlantService.PETT_PLANT_SERVICE_EVENT));
   }

   @Override
   public void onPause() {
      super.onPause();
      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {
         pettPlantService.removeCommStatusListener(myCommunicationManagerListener);
      }

      LocalBroadcastManager.getInstance(this).unregisterReceiver(pettPlantServiceEventReceiver);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar.
      getMenuInflater().inflate(R.menu.menu_main, menu);
//      super.onCreateOptionsMenu(menu);

      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle options menu item clicks here. The action bar will automatically handle clicks
      // on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

      switch (item.getItemId()) {
         case R.id.settings: {
            // Launch the SettingsActivity
            Intent intent = SettingsActivity.createIntent(this);
            startActivity(intent);
            return true;
         }
         case R.id.action_help: {
            Intent intent = HelpActivity.createIntent(this, getActivityName(), fragmentName);
            startActivity(intent);
            return true;
         }
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onStop() {
      super.onStop();

      // Don't stop the service if we are only rotating the device
      if (!isChangingConfigurations()) {
         Intent intent = PettPlantService.createIntent(this);
         stopService(intent);
      }

   }

//   @Override
//   public void onDestroy() {
//      super.onDestroy();
//
//   }

   /**
    * Creates an Intent for this Activity.
    *
    * @param callee
    * @return Intent
    */
   public static Intent createIntent(Context callee) {
      if (MyDebug.LOG) {
         Log.d("MainActivity", "creating main activity");
      }
      return new Intent(callee, MainActivity.class);
   }

   private class MyCommunicationManagerListener implements CommunicationManager.CommunicationManagerListener {

      @Override
      public void onDataRecieved() {}

      @Override
      public void onDataSent() {}

      @Override
      public void onConnecting() {}

      @Override
      public void onConnected() {
         updateActionBar();
      }

      @Override
      public void onDisconnected() {
         updateActionBar();
      }

      @Override
      public void onError(CommunicationErrorType type) {
         if (MyDebug.LOG) {
            Log.d(TAG, "Connection Manager - Error");
         }
      }
   }

   /**
    * Updates the status (sub title) on the action bar.
    */
   private void updateActionBar() {

      CommunicationParams communicationParams = new CommunicationParams(this);

      final ActionBar actionBar = getSupportActionBar();
      if (actionBar == null) {
         return;
      }

      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {
         String message;
         if (pettPlantService.isConnected()) {
            message = getString(R.string.title_connected_to, communicationParams.getName());
         } else {
            message = getString(R.string.title_not_connected);
         }
         actionBar.setSubtitle(message);
      }

   }

   private BroadcastReceiver pettPlantServiceEventReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         String message = intent.getStringExtra("message");
         if (MyDebug.LOG) {
            Log.d("receiver", "Got message: " + message);
         }

         if (message.equals(PettPlantService.PETT_PLANT_SERVICE_CREATED)) {
            PettPlantService pettPlantService = PettPlantService.getInstance();
            if (pettPlantService != null) {
               pettPlantService.addCommStatusListener(myCommunicationManagerListener);
            }

//         } else if (message.equals(PettPlantService.PETT_PLANT_SERVICE_DESTROYED)) {
//            // Save the new comm params
//            CommunicationParams communicationParams = new CommunicationParams(activityContext);
//            boolean success = communicationParams.saveData();
//            final Intent startIntent = PettPlantService.createIntent(activityContext);
//            startService(startIntent);
         }
      }
   };

}
