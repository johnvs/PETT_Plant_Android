package com.biotronisis.pettplant.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.fragment.BluetoothScanFragment;
import com.biotronisis.pettplant.activity.fragment.BluetoothScanFragment.OnBluetoothDeviceSelectedListener;
import com.biotronisis.pettplant.communication.CommunicationErrorType;
import com.biotronisis.pettplant.communication.CommunicationManager.CommunicationManagerListener;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.service.PettPlantService;
import com.biotronisis.pettplant.type.CommunicationType;

/**
 * Created by john on 7/23/15.
 */
public class SettingsActivity extends AbstractBaseActivity {

   private static final String TAG = "SettingsActivity";

   private TextView deviceNameTV;
   private TextView communicationTypeTV;
   private TextView connectionStatusTV;
   private Button bluetoothScanButton;
   private Button usbScanButton;

   private CommunicationParams communicationParams;

   private Context activityContext;
//   private boolean isRegistered;

   private MyCommunicationManagerListener myCommunicationManagerListener =
         new MyCommunicationManagerListener();

   @Override
   public String getActivityName() {
      return TAG;
   }

   @Override
   public String getHelpKey() {
      return "settings";
   }

   public static Intent createIntent(Context context) {
      return new Intent(context, SettingsActivity.class);
   }

   @Override
   protected void onCreate(Bundle savedInstanceSate) {
      super.onCreate(savedInstanceSate);
      setContentView(R.layout.activity_settings);

      bluetoothScanButton = (Button) findViewById(R.id.bluetoothScanButton);
      usbScanButton =       (Button) findViewById(R.id.usbScanButton);
      usbScanButton.setVisibility(View.INVISIBLE);

      communicationTypeTV = (TextView) findViewById(R.id.communicationType);
      deviceNameTV =        (TextView) findViewById(R.id.deviceName);
      connectionStatusTV =  (TextView) findViewById(R.id.connectionStatus);

      communicationParams = new CommunicationParams(this);
      activityContext = this;

      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {
         updateCommunicationDisplay();
         if (pettPlantService.isConnected()) {
            connectionStatusTV.setText(getString(R.string.connected));
         } else {
//                Toast.makeText(getActivity(), getString(R.string.meter_not_connected) , Toast.LENGTH_LONG).show();
            if (pettPlantService.isReConnectingToBtDevice(communicationParams.getAddress())) {
               connectionStatusTV.setText(getString(R.string.attempting_to_reconnect));
            } else {
               connectionStatusTV.setText(getString(R.string.disconnected));
            }
         }
      } else {
         communicationTypeTV.setText(R.string.none);
         deviceNameTV.setText(getString(R.string.none));
         connectionStatusTV.setText(getString(R.string.disconnected));
      }

      bluetoothScanButton.setOnClickListener(new BluetoothScanClickListener());

      ActionBar actionBar = getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);

   }

   @Override
   public void onResume() {
      super.onResume();
      PettPlantService pettPlantService = PettPlantService.getInstance();
      if (pettPlantService != null) {
         pettPlantService.addCommStatusListener(myCommunicationManagerListener);
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

//      myUnregisterReceiver(mUsbReceiver);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar.
//      getMenuInflater().inflate(R.menu.menu_main, menu);
      super.onCreateOptionsMenu(menu);

      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            this.finish();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

//   private void myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
//      if (!isRegistered) {
//         registerReceiver(receiver, filter);
//         isRegistered = true;
//      }
//   }

//   private void myUnregisterReceiver(BroadcastReceiver receiver) {
//      if (isRegistered) {
//         try {
//            unregisterReceiver(receiver);
//            isRegistered = false;
//         } catch (Exception e) {
//            isRegistered = false;
//            if (MyDebug.LOG) {
//               Log.e(TAG, mUsbReceiver + " already unregistered." + e);
//            }
//         }
//      }
//   }

   private void updateCommunicationDisplay() {
      communicationTypeTV.setText(communicationParams.getCommunicationType().name());
      deviceNameTV.setText(communicationParams.getName());
   }

   private class MyCommunicationManagerListener implements CommunicationManagerListener {

      @Override
      public void onDataRecieved() {}

      @Override
      public void onDataSent() {}

      @Override
      public void onConnecting() {}

      @Override
      public void onConnected() {
         updateCommunicationDisplay();
         connectionStatusTV.setText(getString(R.string.connected));
      }

      @Override
      public void onDisconnected() {
         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null && pettPlantService.isReConnectingToBtDevice(communicationParams.getAddress())) {
            connectionStatusTV.setText(getString(R.string.attempting_to_reconnect));
         } else {
            updateCommunicationDisplay();
            connectionStatusTV.setText(getString(R.string.disconnected));
         }
      }

      @Override
      public void onError(CommunicationErrorType type) {
         if (MyDebug.LOG) {
            Log.d(TAG, "Connection Manager - Error");
         }
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

         } else if (message.equals(PettPlantService.PETT_PLANT_SERVICE_DESTROYED)) {
            // Save the new comm params
            boolean success = communicationParams.saveData();
            final Intent startIntent = PettPlantService.createIntent(activityContext);
            startService(startIntent);
         }
      }
   };

   private class BluetoothScanClickListener implements View.OnClickListener {
      public void onClick(View v) {

         BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         if (bluetoothAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
            builder.setTitle(R.string.no_bluetooth_support_title);
            builder.setMessage(R.string.no_bluetooth_support_message);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
            return;
         } else {
            if (!bluetoothAdapter.isEnabled()) {
               AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
               builder.setTitle(R.string.enable_bluetooth_title);
               builder.setMessage(R.string.enable_bluetooth_message);
               builder.setCancelable(false);
               builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                     startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                  }
               });
               builder.show();
               return;
            }
         }

         BluetoothScanFragment dialog = new BluetoothScanFragment();
         dialog.setOnBluetoothDeviceSelectedListener(new OnBluetoothDeviceSelectedListener() {
            @Override
            public void onBluetoothDeviceSelectedListener(BluetoothDevice device) {

               // Check to see if we are trying to reconnect to a BT device
               PettPlantService pettPlantService = PettPlantService.getInstance();
               if (pettPlantService != null && pettPlantService.isReConnectingToBtDevice(device.getAddress())) {
                  // Display toast letting user know we are trying to reconnect to this device
                  Toast.makeText(activityContext, getString(R.string.reconnecting_to_device), Toast.LENGTH_LONG).show();
               } else {
                  final Intent intent = PettPlantService.createIntent(activityContext);
                  stopService(intent);

                  communicationParams.setCommunicationType(CommunicationType.BLUETOOTH);
                  communicationParams.setName(device.getName());
                  communicationParams.setAddress(device.getAddress());
               }

               // This code was moved to pettPlantServiceEventReceiver
               // so it gets executed after the meter service has shut down
//                   communicationParamsDao.updateDefault(communicationParams, new TransactionCallback() {
//
//                       @Override
//                       public void onSuccess() {
//                           getActivity().startService(intent);
//                           MeterService meter = MeterService.getInstance();
//                           if (meter != null) {
//                               meter.addCommStatusListener(myCommunicationManagerListener);
//                           }
//                       }
//
//                       @Override
//                       public void onFailed(Exception e) {
//                           Log.e(TAG, "failed to save communication params", e);
//                       }
//                   });
            }
         });
         dialog.show(getSupportFragmentManager().beginTransaction(), "dialog");
      }
   }
}
