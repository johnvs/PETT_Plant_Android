package com.biotronisis.pettplant.plant.processor;

import com.biotronisis.pettplant.model.ColorMode;
import com.biotronisis.pettplant.model.Entrainment;

import java.io.Serializable;

public class PlantState implements Serializable {
    // Using Serializable to send objects from one activity to another

    private Entrainment.Sequence entrainSequence;
    private Entrainment.State entrainmentState;
    private Entrainment.LoopCheckbox loopCheckbox;
    private ColorMode.Mode colorMode;
    private ColorMode.State colorModeState;
    private int colorModeSpeed;

    public Entrainment.Sequence getEntrainSequence() {
        return entrainSequence;
    }

    void setEntrainSequence(Entrainment.Sequence entrainSequence) {
        this.entrainSequence = entrainSequence;
    }

    public Entrainment.State getEntrainmentState() {
        return entrainmentState;
    }

    void setEntrainmentState(Entrainment.State entrainmentState) {
        this.entrainmentState = entrainmentState;
    }

    public Entrainment.LoopCheckbox getLoopCheckbox() {
        return loopCheckbox;
    }

    void setLoopCheckbox(Entrainment.LoopCheckbox loopCheckbox) {
        this.loopCheckbox = loopCheckbox;
    }

    public ColorMode.Mode getColorMode() {
        return colorMode;
    }

    void setColorMode(ColorMode.Mode colorMode) {
        this.colorMode = colorMode;
    }

    public ColorMode.State getColorModeState() {
        return colorModeState;
    }

    void setColorModeState(ColorMode.State colorModeState) {
        this.colorModeState = colorModeState;
    }

    public int getColorModeSpeed() {
        return colorModeSpeed;
    }

    void setColorModeSpeed(int colorModeSpeed) {
        this.colorModeSpeed = colorModeSpeed;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj.getClass() == PlantState.class) {
            PlantState pS = (PlantState) obj;
            if (this.entrainSequence != pS.entrainSequence)   return false;
            if (this.entrainmentState != pS.entrainmentState) return false;
            if (this.loopCheckbox != pS.loopCheckbox)         return false;
            if (this.colorMode != pS.colorMode)               return false;
            if (this.colorModeState != pS.colorModeState)     return false;
            if (this.colorModeSpeed != pS.colorModeSpeed)     return false;
        } else return false;

        return true;
    }

    @Override
    public String toString() {
        return "PlantState{" +
              "entrainSequence=" + entrainSequence +
              ", entrainmentState=" + entrainmentState +
              ", loopCheckbox=" + loopCheckbox +
              ", colorMode=" + colorMode +
              ", colorModeState=" + colorModeState +
              ", colorModeSpeed=" + colorModeSpeed +
              '}';
    }

    public interface PlantStateGetter {
        PlantState getPlantState();
    }

}
