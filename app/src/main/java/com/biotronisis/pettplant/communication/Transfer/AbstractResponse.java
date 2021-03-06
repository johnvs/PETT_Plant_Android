package com.biotronisis.pettplant.communication.transfer;

import java.io.Serializable;

public abstract class AbstractResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract void fromResponseBytes(byte[] responseBytes);

    public abstract Byte getResponseId();

    public abstract int getMinimumResponseLength();

    public int asInt(byte msb, byte lsb) {
        int msbInt = msb & (0x000000FF);
        int lsbInt = lsb & (0x000000FF);
//		int result = (msbInt << 8) + lsbInt;     // DEBUG
//        return result;
        return (msbInt << 8) + lsbInt;
    }

    int asInt(byte myByte) {
        return myByte & (0x000000FF);
    }

    public boolean validateChecksum(byte[] bytes) {
        boolean result = true;

        if (bytes.length < getMinimumResponseLength()) {
            result = false;
        }

//		int msglen = bytes[0]+1;
        if (bytes.length != bytes[0] + 1) {
            result = false;
        }

        byte checksum = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            checksum ^= bytes[i];
        }

        if (checksum != bytes[bytes.length - 1]) {
            result = false;
        }
        return result;
    }

}

