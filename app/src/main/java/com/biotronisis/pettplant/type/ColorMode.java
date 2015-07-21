package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum ColorMode {

   SOUND_RESPONSIVE(0),
   RAINBOW_CYCLE_1(1),
   RAINBOW_CYCLE_2(2),
   RAINBOW_CYCLE_3(2),
   PRESET_1(3),
   PRESET_2(4),
   PRESET_3(5),
   PRESET_4(6),
   PRESET_5(7);

   public static final int SPEED_DEFAULT = 50;
   public static final String OFF = "off";
   public static final String PAUSE = "pause";

   private int id;

   private ColorMode(int id) {
      this.id = id;
   }

   public int getValue() {
      return id;
   }

   public int getId() {
      return id;
   }

   public static ColorMode getColorMode(int id) {
      return ColorMode.values()[id];
   }
}
