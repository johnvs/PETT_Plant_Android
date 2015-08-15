package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.model.Entrainment;

public class RequestStateResponse extends AbstractResponse {
    private static final long serialVersionUID = 1L;

    public static final Byte RESPONSE_ID = (byte) 0xC0;
    public static final int MIN_RESPONSE_BYTES = 7;       // CommandID, 5 data bytes and checksum

    private Entrainment.Sequence entrainSequence;
    private Entrainment.State entrainmentState;
    private Entrainment.LoopCheckbox loopCheckbox;


    @Override
    public Byte getResponseId() {
        return RESPONSE_ID;
    }

    @Override
    public int getMinimumResponseLength() {
        return MIN_RESPONSE_BYTES;
    }

    @Override
    public void fromResponseBytes(byte[] responseBytes) {

        // Received in range of 65534 - 1, then converted to range 0 - 100%.
//        dutyCycle = 100.0 - ((asInt(responseBytes[2], responseBytes[3])) / 655.34);
    }

//    @Override
//    public String toString() {
//        return "GetPwmDutyCycleResponse [dutyCycle=" + dutyCycle + "]";
//    }
}
