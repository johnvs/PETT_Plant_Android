package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum ColorMode {

   SOUND_RESPONSIVE(0x21),
   RAINBOW_CYCLE_1(0x22),
   RAINBOW_CYCLE_2(0x23),
   RAINBOW_CYCLE_3(0x24),
   PRESET_1(0x25),
   PRESET_2(0x26),
   PRESET_3(0x27),
   PRESET_4(0x28),
   PRESET_5(0x29);

   public static final int SPEED_DEFAULT = 50;

   // Run/Off button values
   public static final String RUN = "Run";
   public static final String OFF = "Off";

   // Pause/Resume button values
   public static final String PAUSE = "Pause";
   public static final String RESUME = "Resume";

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
