package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum EntrainmentMode {

   MEDITATE(0),
   SLEEP(1),
   STAY_AWAKE(3);

   public static final boolean LOOP_CHECKBOX_DEFAULT = false;
   public static final String STOP = "stop";
   public static final String PAUSE = "pause";

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
