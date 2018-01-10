package com.biotronisis.pettplant.communication.transfer;

public class SetSpeedColorModeCommand extends AbstractCommand<EmptyResponse> {
    private static final long serialVersionUID = 1L;

    public static final Byte COMMAND_ID = (byte) 0x30;

    private Byte speed;

    @Override
    public byte[] toCommandBytes() {

        byte[] bytes = new byte[4];
        bytes[0] = 3;                        // Number of message bytes to follow
        bytes[1] = COMMAND_ID;
        bytes[2] = speed;
        bytes[3] = computeChecksum(bytes);

        return bytes;
    }

    @Override
    public Byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Class<EmptyResponse> getResponseClass() {
        return EmptyResponse.class;
    }

    @Override
    public Byte getResponseId() {
        return EmptyResponse.RESPONSE_ID;
    }

    public void setSpeed(Byte speed) {
        this.speed = speed;
    }
}
