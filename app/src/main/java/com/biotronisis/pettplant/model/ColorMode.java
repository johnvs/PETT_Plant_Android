package com.biotronisis.pettplant.model;

/**
 * Created by john on 8/14/15.
 */
public class ColorMode {

   public enum Mode {

      SOUND_RESPONSIVE(0),
      RAINBOW_LOOP_ALL(1),
      RAINBOW_LOOP_WHOLE(2),
      RAINBOW_LOOP_SPECTRUM(3),
      AROUND_THE_WORLD(4),
      RANDOM_POP(5),
      FCK_YEAH_COLORS(6);
//   PRESET_4(7),
//   PRESET_5(8);

      private int id;

      private Mode(int id) {
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
         if (id >= SOUND_RESPONSIVE.getId() && id <= FCK_YEAH_COLORS.getId()) { return true; }
         else { return false; }
      }

      public static Mode getDefault() {
         return SOUND_RESPONSIVE;
      }
   }

   public enum State {

      OFF(0),
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
         if (id >= OFF.getId() && id <= PAUSED.getId()) { return true; }
         else { return false; }
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

      public static boolean isValid(int speed) {
         if (speed > 0 && speed <= 100) { return true;  }
         else                           { return false; }
      }

      public static int getDefault() {
         return SPEED_DEFAULT;
      }
   }

}
