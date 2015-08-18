package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.model.ColorMode;
import com.biotronisis.pettplant.model.Entrainment;

public class RequestStateResponse extends AbstractResponse {
   private static final long serialVersionUID = 1L;

   public static final Byte RESPONSE_ID = (byte) 0xC0;
   public static final int MIN_RESPONSE_BYTES = 7;       // CommandID, 5 data bytes and checksum

   private Entrainment.Sequence entrainSequence;
   private Entrainment.State entrainmentState;
   private Entrainment.LoopCheckbox loopCheckbox;
   private ColorMode.Mode colorMode;
   private ColorMode.State colorModeState;
   private int colorModeSpeed;


   @Override
   public Byte getResponseId() {
      return RESPONSE_ID;
   }

   @Override
   public int getMinimumResponseLength() {
      return MIN_RESPONSE_BYTES;
   }

   @Override
   public void fromResponseBytes(byte[] responseBytes) {

      // data bytes are 2 - 7
      entrainSequence = Entrainment.Sequence.getSequence(responseBytes[2]);
      entrainmentState = Entrainment.State.getState(responseBytes[3]);
      loopCheckbox = Entrainment.LoopCheckbox.getState(responseBytes[4]);
      colorMode = ColorMode.Mode.getMode(responseBytes[5]);
      colorModeState = ColorMode.State.getState(responseBytes[6]);
      colorModeSpeed = asInt(responseBytes[7]);
   }

   public Entrainment.Sequence getEntrainSequence() {
      return entrainSequence;
   }

   public Entrainment.State getEntrainmentState() {
      return entrainmentState;
   }

   public Entrainment.LoopCheckbox getLoopCheckbox() {
      return loopCheckbox;
   }

   public ColorMode.Mode getColorMode() {
      return colorMode;
   }

   public ColorMode.State getColorModeState() {
      return colorModeState;
   }

   public int getColorModeSpeed() {
      return colorModeSpeed;
   }

   @Override
   public String toString() {
      return "RequestStateResponse{" +
            "entrainSequence=" + entrainSequence +
            ", entrainmentState=" + entrainmentState +
            ", loopCheckbox=" + loopCheckbox +
            ", colorMode=" + colorMode +
            ", colorModeState=" + colorModeState +
            ", colorModeSpeed=" + colorModeSpeed +
            '}';
   }

}
