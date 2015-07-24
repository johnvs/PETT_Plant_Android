package com.biotronisis.pettplant.activity;

//import android.app.Activity;
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;

import com.biotronisis.pettplant.R;
//import com.biotronisis.pettplant.activity.fragment.BluetoothScanFragment;
//import com.biotronisis.pettplant.activity.fragment.BluetoothScanFragment.OnBluetoothDeviceSelectedListener;
import com.biotronisis.pettplant.activity.fragment.PettPlantFragment;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
//import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.service.PettPlantService;
//import com.biotronisis.pettplant.type.CommunicationType;

public class MainActivity extends AbstractBaseActivity {

   private static String TAG = "MainActivity";

//   private static final int REQUEST_CONNECT_DEVICE_INSECURE = 1;
//   private static final int REQUEST_ENABLE_BT = 2;

//   private CommunicationParams communicationParams;

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

      Intent intent = PettPlantService.createIntent(this);
      startService(intent);
      if (MyDebug.LOG) {
         Log.d(TAG, "Started PettPlantService.");
      }

      if (savedInstanceState == null) {
         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         PettPlantFragment fragment = new PettPlantFragment();
         transaction.replace(R.id.pett_plant_fragment, fragment);
         transaction.commit();
      }
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
      // Handle action bar item clicks here. The action bar will automatically handle clicks
      // on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

      switch (item.getItemId()) {
//         case R.id.insecure_connect_scan: {
         case R.id.settings: {
            // Launch the SettingsActivity
            Intent intent = SettingsActivity.createIntent(this);    // new Intent(this, SettingsActivity.class);
//            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
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

}
