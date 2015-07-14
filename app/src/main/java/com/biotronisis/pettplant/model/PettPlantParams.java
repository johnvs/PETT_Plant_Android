package com.biotronisis.pettplant.model;

import com.biotronisis.pettplant.type.ColorMode;
import com.biotronisis.pettplant.type.EntrainmentMode;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pettPlantParams")
public class PettPlantParams extends AbstractParamsObject {
	private static final long serialVersionUID = 1L;

	public static final String COLUMN_COLOR_MODE = "colorMode";
	public static final String COLUMN_COLOR_MODE_SPEED = "colorModeSpeed";
	public static final String COLUMN_COLOR_MODE_RUN_BUTTON = "colorModeRunButton";
	public static final String COLUMN_COLOR_MODE_PAUSE_BUTTON = "colorModePauseButton";

	public static final String COLUMN_ENTRAINMENT_SEQUENCE = "entrainmentSequence";
	public static final String COLUMN_ENTRAINMENT_LOOP_CHECKBOX = "entrainmentLoopCheckbox";
	public static final String COLUMN_ENTRAINMENT_RUN_BUTTON = "entrainmentRunButton";
	public static final String COLUMN_ENTRAINMENT_PAUSE_BUTTON = "entrainmentPauseButton";


	@DatabaseField(columnName=COLUMN_COLOR_MODE)
	private ColorMode colorMode;

	@DatabaseField(columnName=COLUMN_COLOR_MODE_SPEED)
	private int colorModeSpeed;

	@DatabaseField(columnName=COLUMN_COLOR_MODE_RUN_BUTTON)
	private String colorModeRunButton;

	@DatabaseField(columnName=COLUMN_COLOR_MODE_PAUSE_BUTTON)
	private String colorModePauseButton;

	@DatabaseField(columnName=COLUMN_ENTRAINMENT_SEQUENCE)
	private EntrainmentMode entrainmentSequence;

	@DatabaseField(columnName=COLUMN_ENTRAINMENT_LOOP_CHECKBOX)
	private String entrainmentLoopCheckbox;

	@DatabaseField(columnName=COLUMN_ENTRAINMENT_RUN_BUTTON)
	private String entrainmentRunButton;

	@DatabaseField(columnName=COLUMN_ENTRAINMENT_PAUSE_BUTTON)
	private String entrainmentPauseButton;


	public ColorMode getColorMode() {
		return colorMode;
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

	public String getEntrainmentLoopCheckbox() {
		return entrainmentLoopCheckbox;
	}

	public void setEntrainmentLoopCheckbox(String entrainmentLoopCheckbox) {
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
