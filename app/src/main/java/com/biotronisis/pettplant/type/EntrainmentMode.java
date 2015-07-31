package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum EntrainmentMode {

   MEDITATE(0x01),
   SLEEP(0x02),
   STAY_AWAKE(0x03);

   // Run/Stop button values
   public static final String RUN = "Run";
   public static final String STOP = "Stop";

   // Pause/Resume button values
   public static final String PAUSE = "Pause";
   public static final String RESUME = "Resume";

   public static final boolean LOOP_CHECKBOX_DEFAULT = false;

   private int id;

   private EntrainmentMode(int id) {
      this.id = id;
   }

   public int getValue() {
      return id;
   }

   public int getId() {
      return id;
   }

   public static EntrainmentMode getEntrainmentMode(int id) {
      return EntrainmentMode.values()[id];
   }
}
