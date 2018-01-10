package com.biotronisis.pettplant.type;

public enum CommunicationType {

    MOCK(0), BLUETOOTH(1), USB(2), TEST(3);

    private int id;

    CommunicationType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public int getId() {
        return id;
    }

    public static CommunicationType getCommType(int id) {
        return CommunicationType.values()[id];
    }
}
