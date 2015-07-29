package com.biotronisis.pettplant.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.persist.AppParams;
import com.biotronisis.pettplant.type.ColorMode;
import com.biotronisis.pettplant.type.EntrainmentMode;

import java.util.logging.Level;

public class PettPlantParams {

	private static final String TAG = "PettPlantParams";

	public static final String COLOR_MODE = "colorMode";
	public static final String COLOR_MODE_SPEED = "colorModeSpeed";
	public static final String COLOR_MODE_RUN_BUTTON = "colorModeRunButton";
	public static final String COLOR_MODE_PAUSE_BUTTON = "colorModePauseButton";

	public static final String ENTRAINMENT_SEQUENCE = "entrainmentSequence";
	public static final String ENTRAINMENT_LOOP_CHECKBOX = "entrainmentLoopCheckbox";
	public static final String ENTRAINMENT_RUN_BUTTON = "entrainmentRunButton";
	public static final String ENTRAINMENT_PAUSE_BUTTON = "entrainmentPauseButton";

   private SharedPreferences appParams;

	private ColorMode colorMode;
	private int colorModeSpeed;
	private String colorModeRunButton;
	private String colorModePauseButton;
	private EntrainmentMode entrainmentSequence;
	private boolean entrainmentLoopCheckbox;
	private String entrainmentRunButton;
	private String entrainmentPauseButton;

	public PettPlantParams(Context context) {
		appParams = context.getSharedPreferences(AppParams.PETT_PLANT_DATA_FILE, 0);

		colorMode = ColorMode.getColorMode(appParams.getInt(COLOR_MODE, ColorMode.SOUND_RESPONSIVE.getValue()));
		colorModeSpeed = appParams.getInt(COLOR_MODE_SPEED, ColorMode.SPEED_DEFAULT);
      colorModeRunButton = appParams.getString(COLOR_MODE_RUN_BUTTON, ColorMode.OFF);
      colorModePauseButton = appParams.getString(COLOR_MODE_PAUSE_BUTTON, ColorMode.PAUSE);

      entrainmentSequence = EntrainmentMode.getEntrainmentMode(appParams.getInt(ENTRAINMENT_SEQUENCE, EntrainmentMode.MEDITATE.getValue()));
      entrainmentLoopCheckbox = appParams.getBoolean(ENTRAINMENT_LOOP_CHECKBOX, EntrainmentMode.LOOP_CHECKBOX_DEFAULT);
      entrainmentRunButton = appParams.getString(ENTRAINMENT_RUN_BUTTON, EntrainmentMode.STOP);
      entrainmentPauseButton = appParams.getString(ENTRAINMENT_PAUSE_BUTTON, EntrainmentMode.PAUSE);

	}


	public ColorMode getColorMode() {
		return colorMode;
	}

   public boolean saveData() {

      SharedPreferences.Editor editor = appParams.edit();

      editor.putInt(COLOR_MODE, colorMode.getValue());
      editor.putInt(COLOR_MODE_SPEED, colorModeSpeed);
      editor.putString(COLOR_MODE_RUN_BUTTON, colorModeRunButton);
      editor.putString(COLOR_MODE_PAUSE_BUTTON, colorModePauseButton);

      editor.putInt(ENTRAINMENT_SEQUENCE, entrainmentSequence.getValue());
      editor.putString(ENTRAINMENT_RUN_BUTTON, entrainmentRunButton);
      editor.putString(ENTRAINMENT_PAUSE_BUTTON, entrainmentPauseButton);
      editor.putBoolean(ENTRAINMENT_LOOP_CHECKBOX, entrainmentLoopCheckbox);

      // Commit the edits!
      boolean success = editor.commit();
      if (!success) {
			if (MyDebug.LOG) {
				Log.e(TAG, "failed to save pettPlantparams");
			}
			ErrorHandler errorHandler = ErrorHandler.getInstance();
         errorHandler.logError(Level.WARNING, "PettPlantParams.saveData(): " +
                     "Can't update pettPlantParams - ",
               R.string.pett_plant_params_persist_error_title,
               R.string.pett_plant_params_persist_error_message);
      }

		return success;
   }

   public void setColorMode(ColorMode colorMode) {
		this.colorMode = colorMode;
	}

	public int getColorModeSpeed() {
		return colorModeSpeed;
	}

	public void setColorModeSpeed(int colorModeSpeed) {
		this.colorModeSpeed = colorModeSpeed;
	}

	public String getColorModeRunButton() {
		return colorModeRunButton;
	}

	public void setColorModeRunButton(String colorModeRunButton) {
		this.colorModeRunButton = colorModeRunButton;
	}

	public String getColorModePauseButton() {
		return colorModePauseButton;
	}

	public void setColorModePauseButton(String colorModePauseButton) {
		this.colorModePauseButton = colorModePauseButton;
	}

	public EntrainmentMode getEntrainmentSequence() {
		return entrainmentSequence;
	}

	public void setEntrainmentSequence(EntrainmentMode entrainmentSequence) {
		this.entrainmentSequence = entrainmentSequence;
	}

	public boolean getEntrainmentLoopCheckbox() {
		return entrainmentLoopCheckbox;
	}

	public void setEntrainmentLoopCheckbox(boolean entrainmentLoopCheckbox) {
		this.entrainmentLoopCheckbox = entrainmentLoopCheckbox;
	}

	public String getEntrainmentRunButton() {
		return entrainmentRunButton;
	}

	public void setEntrainmentRunButton(String entrainmentRunButton) {
		this.entrainmentRunButton = entrainmentRunButton;
	}

	public String getEntrainmentPauseButton() {
		return entrainmentPauseButton;
	}

	public void setEntrainmentPauseButton(String entrainmentPauseButton) {
		this.entrainmentPauseButton = entrainmentPauseButton;
	}

	@Override
	public String toString() {
		return "PettPlantParams{" +
				"colorMode='" + colorMode + '\'' +
				", colorModeSpeed='" + colorModeSpeed + '\'' +
				", colorModeRunButton='" + colorModeRunButton + '\'' +
				", colorModePauseButton='" + colorModePauseButton + '\'' +
				", entrainmentSequence='" + entrainmentSequence + '\'' +
				", entrainmentLoopCheckbox='" + entrainmentLoopCheckbox + '\'' +
				", entrainmentRunButton='" + entrainmentRunButton + '\'' +
				", entrainmentPauseButton='" + entrainmentPauseButton + '\'' +
				'}';
	}

}
