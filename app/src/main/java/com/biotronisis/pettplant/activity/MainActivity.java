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
//import android.widget.Toast;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.fragment.PettPlantFragment;
import com.biotronisis.pettplant.communication.CommunicationErrorType;
import com.biotronisis.pettplant.communication.CommunicationManager;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.plant.PettPlantService;
//import com.biotronisis.pettplant.plant.processor.Plant;
import com.biotronisis.pettplant.plant.processor.PlantState;

//import java.util.logging.Level;

public class MainActivity extends AbstractBaseActivity {

   // This was added on the develop branch.

   private static String TAG = "MainActivity";

   private boolean illBeBack = false;

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
//         Toast.makeText(this, "onCreate: Starting Service", Toast.LENGTH_LONG).show();

         if (MyDebug.LOG) {
            Log.d(TAG, "MainActivity:onCreate - Started PettPlantService.");
         }
//      } else {
//         Toast.makeText(this, "onCreate: Service Already Running", Toast.LENGTH_LONG).show();

//         ErrorHandler errorHandler = ErrorHandler.getInstance();
//         errorHandler.logError(Level.WARNING, "MainActivity.onCreate()$" +
//                     " failed with the error - " + type,
//               R.string.plant_not_responding_title,
//               R.string.plant_not_responding_message);
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

      illBeBack = false;

      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {

         updateActionBar();
         pettPlantService.addCommStatusListener(myCommunicationManagerListener);


      } else {
         Intent intent = PettPlantService.createIntent(this);
         startService(intent);
         if (MyDebug.LOG) {
            Log.d(TAG, "MainActivity:onResume - Started PettPlantService.");
         }
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
         case R.id.action_settings: {
            // Launch the SettingsActivity
            illBeBack = true;

            // If its null on return, we know we didn't get any new plantState in Settings
            PlantState plantState = null;
            Intent intent = SettingsActivity.createIntent(this, plantState);
            startActivityForResult(intent, REQUEST_STATE);
            return true;
         }
         case R.id.action_help: {
            illBeBack = true;
            Intent intent = HelpActivity.createIntent(this, getActivityName(), fragmentName);
            startActivity(intent);
            return true;
         }
         case R.id.action_about: {
            illBeBack = true;
            Intent intent = AboutActivity.createIntent(this);
            startActivity(intent);
            return true;
         }
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_STATE) {
         if (resultCode == RESULT_OK) {
            PlantState plantState = (PlantState) data.getSerializableExtra(EXTRA_PLANT_STATE);

            PettPlantFragment pettPlantFragment = (PettPlantFragment) getSupportFragmentManager().
                  findFragmentById(R.id.pett_plant_fragment);
            if (plantState != null) {
               pettPlantFragment.updateState(plantState);
            }
         }
      }
   }

   @Override
   public void onStop() {
      super.onStop();

      // Don't stop the service if we are only rotating the device
      if (!isChangingConfigurations() && !illBeBack) {
         Intent intent = PettPlantService.createIntent(this);
         stopService(intent);
      }

   }

   @Override
   public void onDestroy() {
      super.onDestroy();

//      if (!isChangingConfigurations()) {
//         Intent intent = PettPlantService.createIntent(this);
//         stopService(intent);
//      }
   }

   /**
    * Creates an Intent for this Activity.
    *
    * @param callee the calling activity
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
            Log.d("Service receiver", "Got message: " + message);
         }

         if (message.equals(PettPlantService.PETT_PLANT_SERVICE_CREATED)) {
            PettPlantService pettPlantService = PettPlantService.getInstance();
            if (pettPlantService != null) {
               pettPlantService.addCommStatusListener(myCommunicationManagerListener);
               updateActionBar();
               if (MyDebug.LOG) {
                  Log.d("Service receiver", "MainActivity:ServiceReceiver:onReceive() - Got PP service.");
               }
            } else {
               if (MyDebug.LOG) {
                  Log.d("Service receiver", "MainActivity:ServiceReceiver:onReceive() - PP service is Null.");
               }
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
