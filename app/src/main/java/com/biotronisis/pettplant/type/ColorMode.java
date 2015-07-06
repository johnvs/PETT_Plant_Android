package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum ColorMode {

    SOUND_RESPONSIVE(0),
    RAINBOW_CYCLE_1(1),
    RAINBOW_CYCLE_2(2),
    PRESET_1(3),
    PRESET_2(4),
    PRESET_3(5),
    PRESET_4(6),
    PRESET_5(7);

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
}
