package com.biotronisis.pettplant.model;

public class ColorMode {

   public enum Mode {

      RAINBOW_LOOP_ALL(0),
      RAINBOW_LOOP_WHOLE(1),
      RAINBOW_LOOP_SPECTRUM(2),
      SOUND_RESPONSIVE(3),
      AROUND_THE_WORLD(4),
      RANDOM_POP(5),
      FCK_YEAH_COLORS(6),
      UP_AND_DOWN(7),
      FIFTY_FIFTY(8);

      private int id;

      Mode(int id) {
         this.id = id;
      }

      public int getValue() {
         return id;
      }

      public int getId() {
         return id;
      }

      public static Mode getMode(int id) {
         return Mode.values()[id];
      }

      public static boolean isValid (int id) {
//         if (id >= RAINBOW_LOOP_ALL.getId() && id <= FIFTY_FIFTY.getId()) { return true; }
          return id >= 0 && id < Mode.values().length;
      }

      public static Mode getDefault() {
         return RAINBOW_LOOP_ALL;
      }
   }

   public enum State {

      OFF(0),
      RUNNING(1),
      PAUSED(2);

      private int id;

      State(int id) {
         this.id = id;
      }

      public int getValue() { return id; }
      public int getId()    { return id; }

      public static State getState(int id) {
         return State.values()[id];
      }

      public static boolean isValid (int id) {
//         if (id >= OFF.getId() && id <= PAUSED.getId()) { return true; }
          return id >= 0 && id < State.values().length;
      }
   }

   public static class RunOffButton {

      // Run/Off button values
      public static final String RUN = "Run";
      public static final String OFF = "Off";

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

   public static class Speed {

      public static final int SPEED_DEFAULT = 50;
      public static final int MAX_ENTRAINTMENT_SPEED = 25;

      public static boolean isValid(int speed) {
          return speed > 0 && speed <= 100;
      }

      public static int getDefault() {
         return SPEED_DEFAULT;
      }
   }

}
