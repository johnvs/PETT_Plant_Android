package com.biotronisis.pettplant.type;

/**
 * Created by john on 6/16/15.
 */
public enum EntrainmentMode {

    MEDITATE(0),
    SLEEP(1),
    LUCID_DREAM(2),
    STAY_AWAKE(3);

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
}
