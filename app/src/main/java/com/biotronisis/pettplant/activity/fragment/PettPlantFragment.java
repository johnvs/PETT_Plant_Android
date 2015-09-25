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

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.AbstractBaseActivity;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.Entrainment;
import com.biotronisis.pettplant.model.PettPlantParams;
import com.biotronisis.pettplant.plant.processor.PlantState;
import com.biotronisis.pettplant.plant.PettPlantService;
import com.biotronisis.pettplant.plant.PettPlantService.PlantStateListener;
import com.biotronisis.pettplant.model.ColorMode;

import java.util.logging.Level;

/**
 * This fragment contains the controls for controlling the PETT Plant
 */
public class PettPlantFragment extends AbstractBaseFragment {

   private static final String TAG = "PettPlantFragment";

   // Entrainment Layout Views
   private Spinner entrainmentSpinner;
   private CheckBox loopCheckbox;
   private Button entrainRunStopButton;
   private Button entrainPauseResumeButton;

   // Color Mode Layout Views
   private Spinner colorModeSpinner;
   private Button colorRunOffButton;
   private Button colorPauseResumeButton;
   private SeekBar colorModeSeekbar;
   private TextView colorModeSpeedTV;

   private PettPlantParams pettPlantParams;
   private PlantState plantState;

   private int lastEntrainmentPos;
   private int lastColorModePos;
   private int lastColorModeSpeed;

   static final String STATE_ENTRAINMENT_MODE = "entrainmentMode";
   static final String STATE_COLOR_MODE = "colorMode";
   static final String STATE_COLOR_MODE_SPEED = "colorModeSpeed";

   private MyPlantStateListener myPlantStateListener = new MyPlantStateListener();

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save the fragment state
      savedInstanceState.putInt(STATE_ENTRAINMENT_MODE, lastEntrainmentPos);
      savedInstanceState.putInt(STATE_COLOR_MODE, lastColorModePos);
      savedInstanceState.putInt(STATE_COLOR_MODE_SPEED, lastColorModeSpeed);

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
   public void setupView(View view, Bundle savedInstanceState) {

      setupEntrainmentControls(view);
      setupColorControls(view);
   }

   public void setupEntrainmentControls(View view) {
      // Setup the Entrainment controls
      entrainmentSpinner = (Spinner) view.findViewById(R.id.spinner_entrainment);
      ArrayAdapter<CharSequence> entrainmentAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.entrainment_array,
            R.layout.cell_modes);
      entrainmentSpinner.setAdapter(entrainmentAdapter);

//    This is in onResume()
//    entrainmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      entrainRunStopButton = (Button) view.findViewById(R.id.button_run_stop);
      entrainRunStopButton.setOnClickListener(new EntrainmentRunStopOnClick());

      entrainPauseResumeButton = (Button) view.findViewById(R.id.button_pause_resume);
      entrainPauseResumeButton.setOnClickListener(new EntrainmentPauseResumeOnClick());

      loopCheckbox = (CheckBox) view.findViewById(R.id.checkbox_loop);
      loopCheckbox.setOnClickListener(new EntrainmentLoopOnClickListener());
   }

   public void setupColorControls(View view) {

      colorModeSpinner = (Spinner) view.findViewById(R.id.spinner_color_mode);
      ArrayAdapter<CharSequence> colorModeAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.color_mode_array,
            R.layout.cell_modes);
      colorModeSpinner.setAdapter(colorModeAdapter);

//    This is in onResume()
//    colorModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      colorRunOffButton = (Button) view.findViewById(R.id.button_color_on_off);
      colorRunOffButton.setOnClickListener(new ColorModeRunOffOnClick());

      colorPauseResumeButton = (Button) view.findViewById(R.id.button_color_pause_resume);
      colorPauseResumeButton.setOnClickListener(new ColorModePauseResumeOnClick());

      colorModeSpeedTV = (TextView) view.findViewById(R.id.value_seekbar);

      colorModeSeekbar = (SeekBar) view.findViewById(R.id.seekbar_speed);

//    This is in onResume()
//      colorModeSeekbar.setOnSeekBarChangeListener(new ColorModeOnSeekbarChange());

   }

   @Override
   public void onPause() {

      PettPlantService plantService = PettPlantService.getInstance();
      if (plantService != null) {
         plantService.removePlantStateListener(myPlantStateListener);
      }

      plantService = null;

      LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pettPlantServiceEventReceiver);

      getActivity().unregisterReceiver(bluetoothReceiver);

      super.onPause();
   }

   @Override
   public void onResume() {
      super.onResume();

      PettPlantService plantService = PettPlantService.getInstance();
      if (plantService != null) {
         plantService.addPlantStateListener(myPlantStateListener);
      }

      LocalBroadcastManager.getInstance(getActivity()).
            registerReceiver(pettPlantServiceEventReceiver,
                  new IntentFilter(PettPlantService.PETT_PLANT_SERVICE_EVENT));

      // Register the Bluetooth BroadcastReceiver
      IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
      getActivity().registerReceiver(bluetoothReceiver, filter); // Don't forget to unregister during onDestroy

      AbstractBaseActivity.fragmentName = this.getClass().getSimpleName();

      // These are here (and not in setupView) because they execute as soon as they are created
      // and their data needs to be populated first.
      entrainmentSpinner.setOnItemSelectedListener(new EntrainmentSpinnerOnItemSelected());
      colorModeSpinner.setOnItemSelectedListener(new ColorModeSpinnerOnItemSelected());
      colorModeSeekbar.setOnSeekBarChangeListener(new ColorModeOnSeekbarChange());
   }

//   @Override
//   public void onDestroy() {
//      super.onDestroy();
//   }

   // Create a BroadcastReceiver for a bluetooth disconnection
   private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         // When discovery finds a device
         if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

            // Update actionbar

//            connectionStatusTV.setText(getString(R.string.disconnected));
            PettPlantService plantService = PettPlantService.getInstance();
            if (plantService != null) {
               plantService.onConnectionLost();
            }
         }
      }
   };

   @Override
   public void populateData() {

      if (plantState != null) {

//         myPlantStateListener.onPlantState(plantState);

      } else {

         pettPlantParams = new PettPlantParams(getActivity());

         entrainmentSpinner.setSelection(pettPlantParams.getEntrainmentSequence().getValue());

         entrainRunStopButton.setText(pettPlantParams.getEntrainmentRunButton());

         if (entrainRunStopButton.getText().equals(Entrainment.RunStopButton.RUN)) {
            entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
            entrainPauseResumeButton.setEnabled(false);
         } else if (entrainRunStopButton.getText().equals(Entrainment.RunStopButton.STOP)) {
            entrainPauseResumeButton.setText(pettPlantParams.getEntrainmentPauseButton());
            entrainPauseResumeButton.setEnabled(true);
         }

         // Convert int to boolean
         loopCheckbox.setChecked(pettPlantParams.getEntrainmentLoopCheckbox().getValue() != 0);

         colorModeSpinner.setSelection(pettPlantParams.getColorMode().getValue());

         colorRunOffButton.setText(pettPlantParams.getColorModeRunButton());

         if (colorRunOffButton.getText().equals(ColorMode.RunOffButton.RUN)) {
            colorPauseResumeButton.setText(pettPlantParams.getColorModePauseButton());
            colorPauseResumeButton.setEnabled(false);
         } else {
            colorPauseResumeButton.setText(pettPlantParams.getColorModePauseButton());
            colorPauseResumeButton.setEnabled(true);
         }

         int speed = pettPlantParams.getColorModeSpeed();
         colorModeSeekbar.setProgress(speed);
         colorModeSpeedTV.setText(Integer.toString(speed));
      }

      lastEntrainmentPos = entrainmentSpinner.getSelectedItemPosition();
      lastColorModePos = colorModeSpinner.getSelectedItemPosition();
      lastColorModeSpeed = colorModeSeekbar.getProgress();

   }

   @Override
   public void persistData() {
      pettPlantParams.setEntrainmentSequence(Entrainment.Sequence.getSequence(entrainmentSpinner.getSelectedItemPosition()));
      pettPlantParams.setEntrainmentRunButton(entrainRunStopButton.getText().toString());
      pettPlantParams.setColorModePauseButton(entrainPauseResumeButton.getText().toString());
      pettPlantParams.setEntrainmentLoopCheckbox(loopCheckbox.isChecked() ?
                  Entrainment.LoopCheckbox.ON : Entrainment.LoopCheckbox.OFF);

      pettPlantParams.setColorMode(ColorMode.Mode.getMode(colorModeSpinner.getSelectedItemPosition()));
      pettPlantParams.setColorModeRunButton(colorRunOffButton.getText().toString());
      pettPlantParams.setColorModePauseButton(colorPauseResumeButton.getText().toString());
      pettPlantParams.setColorModeSpeed(colorModeSeekbar.getProgress());

      pettPlantParams.saveData();
   }

   public void showNoPlantConnectedAlert() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.no_plant_connected_title);
      builder.setMessage(R.string.no_plant_connected_message);
      builder.setPositiveButton(R.string.ok, null);
      builder.show();
   }

   //
   // ----------- Control OnClick Listeners -----------
   //
   private class EntrainmentSpinnerOnItemSelected implements AdapterView.OnItemSelectedListener {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         // The if is needed because the listener fires when the app starts
         if (position != lastEntrainmentPos) {

            Toast entrainmentToast = Toast.makeText(getActivity(),
                        parent.getItemAtPosition(position) + " selected, was " +
                        parent.getItemAtPosition(lastEntrainmentPos),
                  Toast.LENGTH_LONG);
            entrainmentToast.show();

            lastEntrainmentPos = position;
         }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
   }

   private class EntrainmentRunStopOnClick implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current mode of the button
               if (entrainRunStopButton.getText().equals(Entrainment.RunStopButton.RUN)) {

                  // Send the Run command
                  pettPlantService.runEntrainmentSequence(
                        Entrainment.Sequence.getSequence(entrainmentSpinner.getSelectedItemPosition()),
                        new PettPlantService.RunEntrainmentCallback() {

                           @Override
                           public void onSuccess() {
                              Toast.makeText(getActivity(), getString(R.string.run_entrainment_success),
                                    Toast.LENGTH_LONG).show();
                              // Entrainment is now running, so change the Run/Stop button to Stop
                              entrainRunStopButton.setText(Entrainment.RunStopButton.STOP);
                              entrainPauseResumeButton.setEnabled(true);
                              entrainmentSpinner.setEnabled(false);
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "EntrainmentRunStopOnClick.onClick(): Can't run entrainment - " + reason,
                                    R.string.run_entrainment_failed_title,
                                    R.string.run_entrainment_failed_message);
                           }
                        });

               } else if (entrainRunStopButton.getText().equals(Entrainment.RunStopButton.STOP)) {

                  // Send the Stop command
                  pettPlantService.stopEntrainmentSequence(new PettPlantService.StopEntrainmentCallback() {

                     @Override
                     public void onSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.stop_entrainment_success),
                              Toast.LENGTH_LONG).show();
                        // Entrainment is now stopped, so change the Run/Stop button to Run
                        entrainRunStopButton.setText(Entrainment.RunStopButton.RUN);
                        entrainPauseResumeButton.setEnabled(false);
                        entrainmentSpinner.setEnabled(true);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "EntrainmentRunStopOnClick.onClick(): Can't stop entrainment - " + reason,
                              R.string.stop_entrainment_failed_title,
                              R.string.stop_entrainment_failed_message);
                     }
                  });
               }
            } else {
               showNoPlantConnectedAlert();
            }
         }
      }
   }

   private class EntrainmentPauseResumeOnClick implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current mode of the button
               if (entrainPauseResumeButton.getText().equals(Entrainment.PauseResumeButton.PAUSE)) {

                  // Send the Pause command
                  pettPlantService.pauseEntrainmentSequence(
                        new PettPlantService.PauseEntrainmentCallback() {

                           @Override
                           public void onSuccess() {
                              Toast.makeText(getActivity(), getString(R.string.pause_entrainment_success),
                                    Toast.LENGTH_LONG).show();
                              // Entrainment is now pause, so change the Pause/Resume button to Resume
                              entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.RESUME);
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "EntrainmentPauseResumeOnClick.onClick(): Can't pause entrainment - " + reason,
                                    R.string.pause_entrainment_failed_title,
                                    R.string.pause_entrainment_failed_message);
                           }
                        });

               } else if (entrainPauseResumeButton.getText().equals(Entrainment.PauseResumeButton.RESUME)) {

                  // Send the Resume command
                  pettPlantService.resumeEntrainmentSequence(new PettPlantService.ResumeEntrainmentCallback() {

                     @Override
                     public void onSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.resume_entrainment_success),
                              Toast.LENGTH_LONG).show();
                        // Entrainment is now resumed, so change the Pause/Resume button to Pause
                        entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "EntrainmentPauseResumeOnClick.onClick(): Can't resume entrainment - " + reason,
                              R.string.resume_entrainment_failed_title,
                              R.string.resume_entrainment_failed_message);
                     }
                  });
               }
            } else {
               showNoPlantConnectedAlert();
            }
         }
      }
   }

   private class EntrainmentLoopOnClickListener implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current state of the checkbox, which is the NEW state
               if (loopCheckbox.isChecked()) {

                  // Send the Loop On command
                  pettPlantService.loopOnEntrainmentSequence(new PettPlantService.LoopOnEntrainmentCallback() {

                     @Override
                     public void onSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.loop_on_entrainment_success),
                              Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "EntrainmentLoopOnClick.onClick(): Can't loop on entrainment - " + reason,
                              R.string.loop_on_entrainment_failed_title,
                              R.string.loop_on_entrainment_failed_message);
                     }
                  });

               } else {

                  // Send the Loop Off command
                  pettPlantService.loopOffEntrainmentSequence(new PettPlantService.LoopOffEntrainmentCallback() {

                     @Override
                     public void onSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.loop_off_entrainment_success),
                              Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "EntrainmentLoopOnClick.onClick(): Can't loop off entrainment - " + reason,
                              R.string.loop_off_entrainment_failed_title,
                              R.string.loop_off_entrainment_failed_message);
                     }
                  });

               }
            }
         }
      }
   }

   private class ColorModeSpinnerOnItemSelected implements AdapterView.OnItemSelectedListener {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         // The if is needed because the listener fires when the app starts
         if (position != lastColorModePos) {

            Toast colorModeToast = Toast.makeText(getActivity(),
                  parent.getItemAtPosition(position) + " selected, was " +
                        parent.getItemAtPosition(lastColorModePos),
                  Toast.LENGTH_LONG);
            colorModeToast.show();

            lastColorModePos = position;

            PettPlantService pettPlantService = PettPlantService.getInstance();
            if (pettPlantService != null) {
               // Make sure we are connected to a plant before trying to send it the command
               if (pettPlantService.isConnected()) {

                  // Send the Set Color Mode command
                  pettPlantService.setColorMode(
                        ColorMode.Mode.getMode(colorModeSpinner.getSelectedItemPosition()),
                        new PettPlantService.SetColorModeCallback() {

                           @Override
                           public void onSuccess() {
//                              Toast.makeText(getActivity(), getString(R.string.set_color_mode_success),
//                                    Toast.LENGTH_LONG).show();
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "ColorModeSpinnerOnClick.onClick(): Can't set color mode - " + reason,
                                    R.string.set_color_mode_failed_title,
                                    R.string.set_color_mode_failed_message);
                           }
                        }
                  );
               }
            }
         }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
   }

   private class ColorModeRunOffOnClick implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current mode of the button
               if (colorRunOffButton.getText().equals(ColorMode.RunOffButton.RUN)) {

                  // Send the Run command
                  pettPlantService.runColorMode(
                        ColorMode.Mode.getMode(colorModeSpinner.getSelectedItemPosition()),
                        new PettPlantService.RunColorModeCallback() {

                           @Override
                           public void onSuccess() {
//                              Toast.makeText(getActivity(), getString(R.string.run_color_mode_success),
//                                    Toast.LENGTH_LONG).show();
                              // Color Mode is now running, so change the Run/Off button to Off
                              colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
                              colorRunOffButton.setBackgroundResource(R.drawable.button_stop_off_background);
                              colorPauseResumeButton.setEnabled(true);
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "ColorModeRunOffOnClick.onClick(): Can't run color mode - " + reason,
                                    R.string.run_color_mode_failed_title,
                                    R.string.run_color_mode_failed_message);
                           }
                        });

               } else if (colorRunOffButton.getText().equals(ColorMode.RunOffButton.OFF)) {

                  // Send the Off command
                  pettPlantService.offColorMode(new PettPlantService.OffColorModeCallback() {

                     @Override
                     public void onSuccess() {
//                        Toast.makeText(getActivity(), getString(R.string.off_color_mode_success),
//                              Toast.LENGTH_LONG).show();
                        // Color mode is now off, so change the Run/Off button to Run
                        colorRunOffButton.setText(ColorMode.RunOffButton.RUN);
                        colorRunOffButton.setBackgroundResource(R.drawable.button_run_background);
                        colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
                        colorPauseResumeButton.setEnabled(false);
                        colorPauseResumeButton.setBackgroundResource(R.drawable.button_pause_background);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "ColorModeRunOffOnClick.onClick(): Can't turn off color mode - " + reason,
                              R.string.off_color_mode_failed_title,
                              R.string.off_color_mode_failed_message);
                     }
                  });
               }
            } else {
               showNoPlantConnectedAlert();
            }
         }
      }
   }

   private class ColorModePauseResumeOnClick implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current mode of the button
               if (colorPauseResumeButton.getText().equals(ColorMode.PauseResumeButton.PAUSE)) {

                  pettPlantService.pauseColorMode(new PettPlantService.PauseColorModeCallback() {

                     @Override
                     public void onSuccess() {
//                        Toast.makeText(getActivity(), getString(R.string.pause_color_mode_success),
//                              Toast.LENGTH_LONG).show();
                        // Color Mode is now paused, so change the Pause/Resume button to Resume
                        colorPauseResumeButton.setText(ColorMode.PauseResumeButton.RESUME);
                        colorPauseResumeButton.setBackgroundResource(R.drawable.button_resume_background);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "ColorModePauseResumeOnClick.onClick(): Can't pause color mode - " + reason,
                              R.string.pause_color_mode_failed_title,
                              R.string.pause_color_mode_failed_message);
                     }
                  });

               } else if (colorPauseResumeButton.getText().equals(ColorMode.PauseResumeButton.RESUME)) {

                  pettPlantService.resumeColorMode(new PettPlantService.ResumeColorModeCallback() {

                     @Override
                     public void onSuccess() {
//                        Toast.makeText(getActivity(), getString(R.string.resume_color_mode_success),
//                              Toast.LENGTH_LONG).show();
                        // Color Mode is now resumed, so change the Pause/Resume button to Pause
                        colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
                        colorPauseResumeButton.setBackgroundResource(R.drawable.button_pause_background);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "ColorModePauseResumeOnClick.onClick(): Can't resume color mode - " + reason,
                              R.string.resume_color_mode_failed_title,
                              R.string.resume_color_mode_failed_message);
                     }
                  });

               }
            }
         }
      }
   }

   private class ColorModeOnSeekbarChange implements SeekBar.OnSeekBarChangeListener {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
         int speed = progress;

         if (speed == 0) {
            speed = 1;
            colorModeSeekbar.setProgress(speed);
         }

         colorModeSpeedTV.setText(Integer.toString(speed));

         if (speed != lastColorModeSpeed) {
            lastColorModeSpeed = speed;

            PettPlantService pettPlantService = PettPlantService.getInstance();
            if (pettPlantService != null) {
               // Make sure we are connected to a plant before trying to send it the command
               if (pettPlantService.isConnected()) {

                  // Send command
                  pettPlantService.setSpeedColorMode((byte) speed,
                        new PettPlantService.SetSpeedColorModeCallback() {
                           @Override
                           public void onSuccess() {
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "ColorModeOnSeekbarChange(): Can't set color mode speed- " + reason,
                                    R.string.set_speed_color_mode_failed_title,
                                    R.string.set_speed_color_mode_failed_message);
                           }
                        });
               }
            }
         }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {}

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {}
   }

   public void updateState(PlantState pState) {

      if (pState != null) {

         this.plantState = pState;

//         entrainmentSpinner.setSelection(plantState.getEntrainSequence().getValue());
         lastEntrainmentPos = plantState.getEntrainSequence().getValue();
         entrainmentSpinner.setSelection(lastEntrainmentPos);

         switch (plantState.getEntrainmentState()) {
            case STOPPED:
               entrainRunStopButton.setText(Entrainment.RunStopButton.RUN);
               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
               entrainPauseResumeButton.setEnabled(false);
               entrainmentSpinner.setEnabled(true);

               // Make sure any future time indicator gets reset here.

               break;

            case RUNNING:
               entrainRunStopButton.setText(Entrainment.RunStopButton.STOP);
               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
               entrainPauseResumeButton.setEnabled(true);
               entrainmentSpinner.setEnabled(false);

               break;

            case PAUSED:
               entrainRunStopButton.setText(Entrainment.RunStopButton.STOP);
               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.RESUME);
               entrainPauseResumeButton.setEnabled(true);
               entrainmentSpinner.setEnabled(true);

               break;
         }

         // Convert from int to boolean
         loopCheckbox.setChecked(plantState.getLoopCheckbox().getValue() != 0);

         lastColorModePos = plantState.getColorMode().getValue();
         colorModeSpinner.setSelection(lastColorModePos);

         switch (plantState.getColorModeState()) {
            case OFF:
               colorRunOffButton.setText(ColorMode.RunOffButton.RUN);
               colorRunOffButton.setBackgroundResource(R.drawable.button_run_background);

               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
               colorPauseResumeButton.setBackgroundResource(R.drawable.button_pause_background);
               colorPauseResumeButton.setEnabled(false);
               break;

            case RUNNING:
               colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
               colorRunOffButton.setBackgroundResource(R.drawable.button_stop_off_background);

               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
               colorPauseResumeButton.setBackgroundResource(R.drawable.button_pause_background);
               colorPauseResumeButton.setEnabled(true);
               break;

            case PAUSED:
               colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
               colorRunOffButton.setBackgroundResource(R.drawable.button_stop_off_background);

               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.RESUME);
               colorPauseResumeButton.setBackgroundResource(R.drawable.button_resume_background);
               colorPauseResumeButton.setEnabled(true);
               break;
         }

         int speed = plantState.getColorModeSpeed();
         colorModeSeekbar.setProgress(speed);
         colorModeSpeedTV.setText(Integer.toString(speed));
      }
   }

   private class MyPlantStateListener implements PlantStateListener {

      @Override
      public void onPlantState(PlantState pState) {
         if (MyDebug.LOG) {
            Log.d(TAG, "processor state changed. state=" + pState);
         }

//         int entrainSeq = plantState.getEntrainSequence().getValue();
//         if (entrainSeq >= 0 && entrainSeq < Entrainment.Sequence.values().length) {
//            entrainmentSpinner.setSelection(entrainSeq);
//         }

         // All state data was validated prior to this call
         updateState(pState);

//         entrainmentSpinner.setSelection(plantState.getEntrainSequence().getValue());
//
//         switch (plantState.getEntrainmentState()) {
//            case STOPPED:
//               entrainRunStopButton.setText(Entrainment.RunStopButton.RUN);
//               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
//               entrainPauseResumeButton.setEnabled(false);
//
//               // Make sure any future time indicator gets reset here.
//
//               break;
//
//            case RUNNING:
//               entrainRunStopButton.setText(Entrainment.RunStopButton.STOP);
//               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.PAUSE);
//               entrainPauseResumeButton.setEnabled(true);
//
//               break;
//
//            case PAUSED:
//               entrainRunStopButton.setText(Entrainment.RunStopButton.STOP);
//               entrainPauseResumeButton.setText(Entrainment.PauseResumeButton.RESUME);
//               entrainPauseResumeButton.setEnabled(true);
//
//               break;
//         }
//
//         // Convert from int to boolean
//         loopCheckbox.setChecked(plantState.getLoopCheckbox().getValue() != 0);
//
////         int colorMode = plantState.getColorMode().getValue();
////         if (colorMode >= 0 && colorMode < ColorMode.Mode.values().length) {
////            colorModeSpinner.setSelection(colorMode);
////         }
//
//         colorModeSpinner.setSelection(plantState.getColorMode().getValue());
//
//         switch (plantState.getColorModeState()) {
//            case OFF:
//               colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
//               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
//               colorPauseResumeButton.setEnabled(false);
//               break;
//
//            case RUNNING:
//               colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
//               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.PAUSE);
//               colorPauseResumeButton.setEnabled(true);
//               break;
//
//            case PAUSED:
//               colorRunOffButton.setText(ColorMode.RunOffButton.OFF);
//               colorPauseResumeButton.setText(ColorMode.PauseResumeButton.RESUME);
//               colorPauseResumeButton.setEnabled(true);
//               break;
//         }
//
//         colorModeSeekbar.setProgress(plantState.getColorModeSpeed());

      }

      @Override
      public void onError(String reason) {
         Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
         if (MyDebug.LOG) {
            Log.d(TAG, "processor state failed. " + reason);
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
               pettPlantService.addPlantStateListener(myPlantStateListener);
            }
//         } else if (message.equals(PettPlantService.PETT_PLANT_SERVICE_DESTROYED)) {
//            // Save the new comm params
//            boolean success = communicationParams.saveData();
//            final Intent startIntent = PettPlantService.createIntent(activityContext);
//            startService(startIntent);
         }
      }
   };

}
