package com.biotronisis.pettplant.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.persist.AppParams;

import java.util.logging.Level;

public class PettPlantParams {

	private static final String TAG = "PettPlantParams";

	public static final String ENTRAINMENT_SEQUENCE = "entrainmentSequence";
	public static final String ENTRAINMENT_RUN_BUTTON = "entrainmentRunButton";
	public static final String ENTRAINMENT_PAUSE_BUTTON = "entrainmentPauseButton";
	public static final String ENTRAINMENT_LOOP_CHECKBOX = "entrainmentLoopCheckbox";

	public static final String COLOR_MODE = "colorMode";
	public static final String COLOR_MODE_SPEED = "colorModeSpeed";
	public static final String COLOR_MODE_RUN_BUTTON = "colorModeRunButton";
	public static final String COLOR_MODE_PAUSE_BUTTON = "colorModePauseButton";

   private SharedPreferences appParams;

	private Entrainment.Sequence entrainmentSequence;
	private String entrainmentRunButton;
	private String entrainmentPauseButton;
	private Entrainment.LoopCheckbox entrainmentLoopCheckbox;

	private ColorMode.Mode colorMode;
	private String colorModeRunButton;
	private String colorModePauseButton;
	private int colorModeSpeed;

	public PettPlantParams(Context context) {
		appParams = context.getSharedPreferences(AppParams.PETT_PLANT_DATA_FILE, 0);

		int seq = appParams.getInt(ENTRAINMENT_SEQUENCE, Entrainment.Sequence.getDefault().getValue());
		if (Entrainment.Sequence.isValid(seq)) {
			entrainmentSequence = Entrainment.Sequence.getSequence(seq);
		} else {
			entrainmentSequence = Entrainment.Sequence.getDefault();
		}

		String str = appParams.getString(ENTRAINMENT_RUN_BUTTON, Entrainment.RunStopButton.getDefault());
		if (str.equals(Entrainment.RunStopButton.RUN) || str.equals(Entrainment.RunStopButton.STOP)) {
			entrainmentRunButton = str;
		} else {
			entrainmentRunButton = Entrainment.RunStopButton.getDefault();
		}

		str = appParams.getString(ENTRAINMENT_PAUSE_BUTTON, Entrainment.PauseResumeButton.getDefault());
		if (str.equals(Entrainment.PauseResumeButton.PAUSE) || str.equals(Entrainment.PauseResumeButton.RESUME)) {
			entrainmentPauseButton = str;
		} else {
			entrainmentPauseButton = Entrainment.PauseResumeButton.getDefault();
		}

		int checkbox = appParams.getInt(ENTRAINMENT_LOOP_CHECKBOX,
				Entrainment.LoopCheckbox.OFF.getValue());
		if (Entrainment.LoopCheckbox.isValid(checkbox)) {
			entrainmentLoopCheckbox = Entrainment.LoopCheckbox.getState(checkbox);
		} else {
			entrainmentLoopCheckbox = Entrainment.LoopCheckbox.OFF;
		}

		int mode = appParams.getInt(COLOR_MODE, ColorMode.Mode.SOUND_RESPONSIVE.getValue());
		if (ColorMode.Mode.isValid(mode)) {
			colorMode = ColorMode.Mode.getMode(mode);
		} else {
			colorMode = ColorMode.Mode.SOUND_RESPONSIVE;
		}

		int speed = appParams.getInt(COLOR_MODE_SPEED, ColorMode.Speed.getDefault());
		if (ColorMode.Speed.isValid(speed)) {
			colorModeSpeed = speed;
		} else {
			colorModeSpeed = ColorMode.Speed.getDefault();
		}

		str = appParams.getString(COLOR_MODE_RUN_BUTTON, ColorMode.RunOffButton.getDefault());
		if (str.equals(ColorMode.RunOffButton.RUN) || str.equals(ColorMode.RunOffButton.OFF)) {
			colorModeRunButton = str;
		} else {
			colorModeRunButton = ColorMode.RunOffButton.getDefault();
		}

		str = appParams.getString(COLOR_MODE_PAUSE_BUTTON, ColorMode.PauseResumeButton.getDefault());
		if (str.equals(ColorMode.PauseResumeButton.PAUSE) || str.equals(ColorMode.PauseResumeButton.RESUME)) {
			colorModePauseButton = str;
		} else {
			colorModePauseButton = ColorMode.PauseResumeButton.getDefault();
		}
	}

	public ColorMode.Mode getColorMode() {
		return colorMode;
	}

   public boolean saveData() {

      SharedPreferences.Editor editor = appParams.edit();

		editor.putInt(ENTRAINMENT_SEQUENCE, entrainmentSequence.getValue());
		editor.putString(ENTRAINMENT_RUN_BUTTON, entrainmentRunButton);
		editor.putString(ENTRAINMENT_PAUSE_BUTTON, entrainmentPauseButton);
		editor.putInt(ENTRAINMENT_LOOP_CHECKBOX, entrainmentLoopCheckbox.getValue());

		editor.putInt(COLOR_MODE, colorMode.getValue());
      editor.putInt(COLOR_MODE_SPEED, colorModeSpeed);
      editor.putString(COLOR_MODE_RUN_BUTTON, colorModeRunButton);
      editor.putString(COLOR_MODE_PAUSE_BUTTON, colorModePauseButton);

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

   public void setColorMode(ColorMode.Mode cMode) {
		this.colorMode = cMode;
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

	public Entrainment.Sequence getEntrainmentSequence() {
		return entrainmentSequence;
	}

	public void setEntrainmentSequence(Entrainment.Sequence entrainmentSequence) {
		this.entrainmentSequence = entrainmentSequence;
	}

	public Entrainment.LoopCheckbox getEntrainmentLoopCheckbox() {
		return entrainmentLoopCheckbox;
	}

	public void setEntrainmentLoopCheckbox(Entrainment.LoopCheckbox entrainmentLoopCheckbox) {
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
