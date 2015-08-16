package com.biotronisis.pettplant.plant.processor;

import com.biotronisis.pettplant.model.ColorMode;
import com.biotronisis.pettplant.model.Entrainment;

/**
 * Created by john on 8/15/15.
 */
public class PlantState {

   private Entrainment.Sequence entrainSequence;
   private Entrainment.State entrainmentState;
   private Entrainment.LoopCheckbox loopCheckbox;
   private ColorMode.Mode colorMode;
   private ColorMode.State colorModeState;
   private int colorModeSpeed;

   public Entrainment.Sequence getEntrainSequence() {
      return entrainSequence;
   }

   public void setEntrainSequence(Entrainment.Sequence entrainSequence) {
      this.entrainSequence = entrainSequence;
   }

   public Entrainment.State getEntrainmentState() {
      return entrainmentState;
   }

   public void setEntrainmentState(Entrainment.State entrainmentState) {
      this.entrainmentState = entrainmentState;
   }

   public Entrainment.LoopCheckbox getLoopCheckbox() {
      return loopCheckbox;
   }

   public void setLoopCheckbox(Entrainment.LoopCheckbox loopCheckbox) {
      this.loopCheckbox = loopCheckbox;
   }

   public ColorMode.Mode getColorMode() {
      return colorMode;
   }

   public void setColorMode(ColorMode.Mode colorMode) {
      this.colorMode = colorMode;
   }

   public ColorMode.State getColorModeState() {
      return colorModeState;
   }

   public void setColorModeState(ColorMode.State colorModeState) {
      this.colorModeState = colorModeState;
   }

   public int getColorModeSpeed() {
      return colorModeSpeed;
   }

   public void setColorModeSpeed(int colorModeSpeed) {
      this.colorModeSpeed = colorModeSpeed;
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
}
