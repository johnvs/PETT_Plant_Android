/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biotronisis.pettplant.activity.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.AbstractBaseActivity;
import com.biotronisis.pettplant.activity.DeviceListActivity;
import com.biotronisis.pettplant.communication.BluetoothCommAdapter;
import com.biotronisis.pettplant.communication.ConnectionState;
import com.biotronisis.pettplant.model.PettPlantParams;
import com.biotronisis.pettplant.type.ColorMode;
import com.biotronisis.pettplant.type.EntrainmentMode;

import java.lang.ref.WeakReference;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class PettPlantFragment extends AbstractBaseFragment {

   private static final String TAG = "PettPlantFragment";
//   public static String fragmentName = null;

   // Intent request codes
   private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
   private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
   private static final int REQUEST_ENABLE_BT = 3;

   // Entrainment Layout Views
   private Spinner entrainmentSpinner;
   private CheckBox loopCheckbox;
   private Button entrainRunStopButton;
   private Button entrainPauseResumeButton;

   // Color Mode Layout Views
   private Spinner colorModeSpinner;
   private SeekBar colorModeSeekbar;
   private Button colorRunOffButton;
   private Button colorPauseResumeButton;

   private PettPlantParams pettPlantParams;

   private int lastEntrainmentPos;
   private int lastColorModePos;

   // Name of the connected device
   private String mConnectedDeviceName = null;

   // String buffer for outgoing messages
   private StringBuffer mOutStringBuffer;

   // Local Bluetooth adapter
   private BluetoothAdapter mBluetoothAdapter = null;

   // Member object for the bluetooth services
   private BluetoothCommAdapter bluetoothCommAdapter = null;

   static final String STATE_ENTRAINMENT_MODE = "entrainmentMode";
   static final String STATE_COLOR_MODE = "colorMode";

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save the fragment state
      savedInstanceState.putInt(STATE_ENTRAINMENT_MODE, lastEntrainmentPos);
      savedInstanceState.putInt(STATE_COLOR_MODE, lastColorModePos);

      // Always call the superclass so it can save the view hierarchy state
      super.onSaveInstanceState(savedInstanceState);
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

   }

   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
      View theView = inflater.inflate(R.layout.fragment_pett_plant, container, false);
      return theView;
   }

   @Override
   public void onStart() {
      super.onStart();
      // If BT is not on, request that it be enabled.
      // setupBluetoothClient() will then be called during onActivityResult
//      if (!mBluetoothAdapter.isEnabled()) {
//         Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//         startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//         // Otherwise, setup the chat session
//      } else if (bluetoothCommAdapter == null) {
//         //Todo
////         setupBluetoothClient();
//      }
   }

   @Override
   public void onResume() {
      super.onResume();

      setHasOptionsMenu(true);
      // Get local Bluetooth adapter
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

      // If the adapter is null, then Bluetooth is not supported
      if (mBluetoothAdapter == null) {
         FragmentActivity activity = getActivity();
         Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
         activity.finish();
      }

      AbstractBaseActivity.fragmentName = this.getClass().getSimpleName();

      // Performing this check in onResume() covers the case in which BT was
      // not enabled during onStart(), so we were paused to enable it...
      // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
      if (bluetoothCommAdapter != null) {
         // Only if the state is STATE_NONE, do we know that we haven't started already
         if (bluetoothCommAdapter.getState() == ConnectionState.NONE) {
            // Start the Bluetooth client
            // Todo
//            bluetoothCommAdapter.activate();
         }
      }

      entrainmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // The if is needed because the listener fires when the app starts
            if (position != lastEntrainmentPos) {
               lastEntrainmentPos = position;
               if (position < EntrainmentMode.values().length) {  // Check your bounds!
                  EntrainmentMode whichMode = EntrainmentMode.values()[position];

                  switch (whichMode) {
                     case MEDITATE:


                  }

               }
               Toast entrainmentToast = Toast.makeText(getActivity(),
                     parent.getItemAtPosition(position) + " selected",
                     Toast.LENGTH_LONG);
               entrainmentToast.show();
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });

      colorModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // The if is needed because the listener fires when the app starts
            if (position != lastColorModePos) {
               lastColorModePos = position;
               Toast colorModeToast = Toast.makeText(getActivity(),
                     parent.getItemAtPosition(position) + " selected",
                     Toast.LENGTH_LONG);
               colorModeToast.show();
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      if (bluetoothCommAdapter != null) {
         bluetoothCommAdapter.deactivate();
      }
   }

   @Override
   public void populateData() {
      pettPlantParams = new PettPlantParams(getActivity());

      entrainmentSpinner.setSelection(pettPlantParams.getEntrainmentSequence().getValue());
      entrainRunStopButton.setText(pettPlantParams.getEntrainmentRunButton());
      entrainPauseResumeButton.setText(pettPlantParams.getEntrainmentPauseButton());
      loopCheckbox.setChecked(pettPlantParams.getEntrainmentLoopCheckbox());

      colorModeSpinner.setSelection(pettPlantParams.getColorMode().getValue());
      colorModeSeekbar.setProgress(pettPlantParams.getColorModeSpeed());
      colorRunOffButton.setText(pettPlantParams.getColorModeRunButton());
      colorPauseResumeButton.setText(pettPlantParams.getColorModePauseButton());

      lastEntrainmentPos = entrainmentSpinner.getSelectedItemPosition();
      lastColorModePos = colorModeSpinner.getSelectedItemPosition();

   }

   @Override
   public void persistData() {
      pettPlantParams.setEntrainmentSequence(EntrainmentMode.getEntrainmentMode(entrainmentSpinner.getSelectedItemPosition()));
      pettPlantParams.setEntrainmentRunButton(entrainRunStopButton.getText().toString());
      pettPlantParams.setColorModePauseButton(entrainPauseResumeButton.getText().toString());
      pettPlantParams.setEntrainmentLoopCheckbox(loopCheckbox.isChecked());

      pettPlantParams.setColorMode(ColorMode.getColorMode(colorModeSpinner.getSelectedItemPosition()));
      pettPlantParams.setColorModeRunButton(colorRunOffButton.getText().toString());
      pettPlantParams.setColorModePauseButton(colorPauseResumeButton.getText().toString());
      pettPlantParams.setColorModeSpeed(colorModeSeekbar.getProgress());

      pettPlantParams.saveData();
   }

   @Override
   public void setupView(View view, Bundle savedInstanceState) {

      // Setup the Entrainment controls
      entrainmentSpinner = (Spinner) view.findViewById(R.id.spinner_entrainment);
      ArrayAdapter<CharSequence> entrainmentAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.entrainment_array,
            R.layout.cell_modes);
      entrainmentSpinner.setAdapter(entrainmentAdapter);

      entrainRunStopButton = (Button) view.findViewById(R.id.button_run_stop);

      entrainPauseResumeButton = (Button) view.findViewById(R.id.button_pause_resume);
      loopCheckbox = (CheckBox) view.findViewById(R.id.checkbox_loop);

      colorModeSpinner = (Spinner) view.findViewById(R.id.spinner_color_mode);
      ArrayAdapter<CharSequence> colorModeAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.color_mode_array,
            R.layout.cell_modes);
      colorModeSpinner.setAdapter(colorModeAdapter);

      colorModeSeekbar = (SeekBar) view.findViewById(R.id.seekbar_speed);
      colorRunOffButton = (Button) view.findViewById(R.id.button_color_on_off);
      colorPauseResumeButton = (Button) view.findViewById(R.id.button_color_pause_resume);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.menu_pett_plant, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.insecure_connect_scan: {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
         }
      }
      return false;
   }

   /**
    * Set up the UI and background operations for chat.
    */
/*
   private void setupBluetoothClient() {
      Log.d(TAG, "setupBluetoothClient()");

      // Initialize view controls listeners
      entrainRunStopButton.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            // Send a message using content of the edit text widget
            View view = getView();
            if (null != view) {
               String message = "Test";
               sendMessage(message);
            }
         }
      });

      // Initialize the BluetoothCommAdapter to perform bluetooth connections
//      bluetoothCommAdapter = new BluetoothCommAdapter(getActivity(), PettPlantFragment.mHandler);
      bluetoothCommAdapter = new BluetoothCommAdapter(mHandler);

      // Initialize the buffer for outgoing messages
      mOutStringBuffer = new StringBuffer("");
   }
*/

   /**
    * Sends a message.
    *
    * @param message A string of text to send.
    */
   private void sendMessage(String message) {
      // Check that we're actually connected before trying anything
      if (bluetoothCommAdapter.getState() != ConnectionState.ESTABLISHED) {
         Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
         return;
      }

      // Check that there's actually something to send
      if (message.length() > 0) {
         // Get the message bytes and tell the BluetoothCommAdapter to write
         byte[] send = message.getBytes();
         bluetoothCommAdapter.write(send);

         // Reset out string buffer to zero and clear the edit text field
         mOutStringBuffer.setLength(0);
      }
   }

   /**
    * The action listener for the EditText widget, to listen for the return key
    */
//    private TextView.OnEditorActionListener mWriteListener
//            = new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            // If the action is a key-up event on the return key, send the message
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            return true;
//        }
//    };

   /**
    * Updates the status on the action bar.
    *
    * @param resId a string resource ID
    */
   private void setStatus(int resId) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      if (null == activity) {
         return;
      }

      final ActionBar actionBar = activity.getSupportActionBar();
      if (null == actionBar) {
         return;
      }
      actionBar.setSubtitle(resId);
   }

   /**
    * Updates the status on the action bar.
    *
    * @param subTitle status
    */
   private void setStatus(CharSequence subTitle) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      if (null == activity) {
         return;
      }

      final ActionBar actionBar = activity.getSupportActionBar();
      if (null == actionBar) {
         return;
      }
      actionBar.setSubtitle(subTitle);
   }

   /**
    * The Handler that gets information back from the BluetoothCommAdapter
    */
   static class MyInnerHandler extends Handler {
      AppCompatActivity mActivity;
      WeakReference<PettPlantFragment> mFrag;

      MyInnerHandler(AppCompatActivity aActivity, PettPlantFragment aFrag) {
         mActivity = aActivity;
         mFrag = new WeakReference<PettPlantFragment>(aFrag);
      }

      @Override
      public void handleMessage(Message msg) {
//         FragmentActivity activity = getActivity();
         PettPlantFragment theFrag = mFrag.get();

         switch (msg.what) {
            case BluetoothCommAdapter.MESSAGE_STATE_CHANGE:
//               switch (msg.arg1) {
//                  case BluetoothCommAdapter.STATE_CONNECTED:
//                     theFrag.setStatus(theFrag.getString(R.string.title_connected_to, theFrag.mConnectedDeviceName));
//                     break;
//                  case BluetoothCommAdapter.STATE_CONNECTING:
//                     theFrag.setStatus(R.string.title_connecting);
//                     break;
//                  case BluetoothCommAdapter.STATE_LISTEN:
//                  case BluetoothCommAdapter.STATE_NONE:
//                     theFrag.setStatus(R.string.title_not_connected);
//                     break;
//               }
               break;
            case BluetoothCommAdapter.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
               // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
               break;
            case BluetoothCommAdapter.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
               // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
               break;
            case BluetoothCommAdapter.MESSAGE_DEVICE_NAME:
               // save the connected device's name
               theFrag.mConnectedDeviceName = msg.getData().getString(BluetoothCommAdapter.DEVICE_NAME);
               if (mActivity != null) {
                  Toast.makeText(mActivity, "Connected to "
                        + theFrag.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
               }
               break;
            case BluetoothCommAdapter.MESSAGE_TOAST:
               if (mActivity != null) {
                  Toast.makeText(mActivity, msg.getData().getString(BluetoothCommAdapter.TOAST),
                        Toast.LENGTH_SHORT).show();
               }
               break;
         }
      }
   }

   MyInnerHandler mHandler = new MyInnerHandler((AppCompatActivity) getActivity(), this);
//   private static final Handler mHandler = new Handler() {

//   };

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
         case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
               connectDevice(data, false);
            }
            break;

         case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
               // Bluetooth is now enabled, so set up a chat session
               // Todo
//               setupBluetoothClient();
            } else {
               // User did not enable Bluetooth or an error occurred
               Log.d(TAG, "BT not enabled");
               Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                     Toast.LENGTH_SHORT).show();
            }
      }
   }

   /**
    * Establish connection with the device
    *
    * @param data   An {@link Intent} with {@link com.biotronisis.pettplant.activity.DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
    * @param secure Socket Security type - Secure (true) , Insecure (false)
    */
   private void connectDevice(Intent data, boolean secure) {
      // Get the device MAC address
      String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
      // Get the BluetoothDevice object
      BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
      // Attempt to connect to the device
      bluetoothCommAdapter.connect(device, secure);
   }

}
