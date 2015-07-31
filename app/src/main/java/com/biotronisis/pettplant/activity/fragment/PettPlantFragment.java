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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.PettPlantParams;
import com.biotronisis.pettplant.service.PettPlantService;
import com.biotronisis.pettplant.type.ColorMode;
import com.biotronisis.pettplant.type.EntrainmentMode;

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
   private SeekBar colorModeSeekbar;
   private Button colorRunOffButton;
   private Button colorPauseResumeButton;

   private PettPlantParams pettPlantParams;

   private int lastEntrainmentPos;
   private int lastColorModePos;

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
   public void setupView(View view, Bundle savedInstanceState) {

      // Setup the Entrainment controls
      entrainmentSpinner = (Spinner) view.findViewById(R.id.spinner_entrainment);
      ArrayAdapter<CharSequence> entrainmentAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.entrainment_array,
            R.layout.cell_modes);
      entrainmentSpinner.setAdapter(entrainmentAdapter);

      entrainRunStopButton = (Button) view.findViewById(R.id.button_run_stop);
      entrainRunStopButton.setOnClickListener(new EntrainmentRunStopOnClick());

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
   public void onResume() {
      super.onResume();

      AbstractBaseActivity.fragmentName = this.getClass().getSimpleName();

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

   public void showNoPlantConnectedAlert() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.no_plant_connected_title);
      builder.setMessage(R.string.no_plant_connected_message);
      builder.setPositiveButton(R.string.ok, null);
      builder.show();
   }

   private class EntrainmentRunStopOnClick implements View.OnClickListener {

      @Override
      public void onClick(View v) {

         PettPlantService pettPlantService = PettPlantService.getInstance();
         if (pettPlantService != null) {
            // Make sure we are connected to a plant before trying to send it the command
            if (pettPlantService.isConnected()) {
               // Check the current mode of the button
               if (entrainRunStopButton.getText().equals(EntrainmentMode.RUN)) {

                  // Send the Run command
                  pettPlantService.runEntrainmentSequence(
                        EntrainmentMode.getEntrainmentMode(entrainmentSpinner.getSelectedItemPosition()),
                        new PettPlantService.RunEntrainmentCallback() {

                     @Override
                     public void onSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.run_entrainment_success),
                              Toast.LENGTH_LONG).show();
                        // Entrainment is now running, so change the Run/Stop button to Stop
                        entrainRunStopButton.setText(EntrainmentMode.STOP);
                     }

                     @Override
                     public void onFailed(String reason) {
                        ErrorHandler errorHandler = ErrorHandler.getInstance();
                        errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                    "SetEntrainmentRunOnClick.onClick(): Can't run entrainment - " + reason,
                              R.string.run_entrainment_failed_title,
                              R.string.run_entrainment_failed_message);
                     }
                  });
               } else if (entrainRunStopButton.getText().equals(EntrainmentMode.STOP)) {

                  // Send the Stop command
                  pettPlantService.stopEntrainmentSequence(new PettPlantService.StopEntrainmentCallback() {

                           @Override
                           public void onSuccess() {
                              Toast.makeText(getActivity(), getString(R.string.stop_entrainment_success),
                                    Toast.LENGTH_LONG).show();
                              // Entrainment is now stopped, so change the Run/Stop button to Run
                              entrainRunStopButton.setText(EntrainmentMode.RUN);
                           }

                           @Override
                           public void onFailed(String reason) {
                              ErrorHandler errorHandler = ErrorHandler.getInstance();
                              errorHandler.logError(Level.WARNING, "PettPlantFragment$" +
                                          "SetEntrainmentStopOnClick.onClick(): Can't stop entrainment - " + reason,
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

}
