package com.biotronisis.pettplant.communication.transfer;

public class ResumeColorModeCommand extends AbstractCommand<EmptyResponse> {
    private static final long serialVersionUID = 1L;

    public static final Byte COMMAND_ID = (byte) 0x34;

    @Override
    public byte[] toCommandBytes() {

        byte[] bytes = new byte[3];
        bytes[0] = 2;                        // Number of message bytes to follow
        bytes[1] = COMMAND_ID;
        bytes[2] = computeChecksum(bytes);

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

}
