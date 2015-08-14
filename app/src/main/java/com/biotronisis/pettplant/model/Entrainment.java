package com.biotronisis.pettplant.model;

/**
 * Created by john on 8/14/15.
 */
public class Entrainment {

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
   }

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
   }

   // Run/Stop button values
   public static final String RUN = "Run";
   public static final String STOP = "Stop";

   // Pause/Resume button values
   public static final String PAUSE = "Pause";
   public static final String RESUME = "Resume";

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
   }

   // Loop checkbox values
   public static final String LOOP_OFF = "Off";
   public static final String LOOP_ON = "On";

//   public static final boolean LOOP_CHECKBOX_DEFAULT = false;

//   private EntrainmentMode entrainmentSequence;

}
