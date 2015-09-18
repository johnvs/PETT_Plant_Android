package com.biotronisis.pettplant.model;

/**
 * Created by john on 8/14/15.
 */
public class Entrainment {

   public enum Sequence {

      MEDITATE(0),
      SLEEP(1),
      STAY_AWAKE(2);

      private int id;

      private Sequence(int id) {
         this.id = id;
      }

      public int getValue() { return id; }
      public int getId()    { return id; }

      public static Sequence getSequence(int id) {
         return Sequence.values()[id];
      }

      public static boolean isValid (int id) {
//         if (id >= MEDITATE.getId() && id <= STAY_AWAKE.getId()) { return true;  }
         if (id >= 0 && id < Sequence.values().length) { return true;  }
         else { return false; }
      }

      public static Sequence getDefault() {
         return MEDITATE;
      }
   }

   public enum State {

      STOPPED(0),
      RUNNING(1),
      PAUSED(2);

      private int id;

      private State(int id) {
         this.id = id;
      }

      public int getValue() { return id; }
      public int getId()    { return id; }

      public static State getState(int id) {
         return State.values()[id];
      }

      public static boolean isValid (int id) {
//         if (id >= STOPPED.getId() && id <= PAUSED.getId()) { return true; }
         if (id >= 0 && id < State.values().length) { return true; }
         else { return false; }
      }
   }

   public static class RunStopButton {

      // Run/Stop button values
      public static final String RUN = "Run";
      public static final String STOP = "Stop";

      public static String getDefault() {
         return RUN;
      }
   }

   public static class PauseResumeButton {

      // Pause/Resume button values
      public static final String PAUSE = "Pause";
      public static final String RESUME = "Resume";

      public static String getDefault() {
         return PAUSE;
      }
   }

   public enum LoopCheckbox {

      OFF(0),
      ON(1);

      private int id;

      private LoopCheckbox(int id) {
         this.id = id;
      }

      public int getValue() { return id; }
      public int getId()    { return id; }

      public static LoopCheckbox getState(int id) {
         return LoopCheckbox.values()[id];
      }

      public static boolean isValid (int id) {
//         if (id >= OFF.getId() && id <= ON.getId()) { return true; }
         if (id >= 0 && id < LoopCheckbox.values().length) { return true; }
         else { return false; }
      }

      public static LoopCheckbox getDefault() {
         return OFF;
      }
   }

}
