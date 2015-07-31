package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.type.EntrainmentMode;

public class RunEntrainmentCommand extends AbstractCommand<EmptyResponse> {
	private static final long serialVersionUID = 1L;
	
	public static final Byte COMMAND_ID = (byte)0x10;
	
	private EntrainmentMode eMode;
	
	@Override
	public byte[] toCommandBytes() {

		byte[] bytes = new byte[4];
		bytes[0] = 3;                        // Number of message bytes to follow
		bytes[1] = COMMAND_ID;
		bytes[2] = (byte)(eMode.getValue() + 0x01); // Add 0x01 to translate the sequence numbers into command data format
		                                            // See ?? for plant command structure
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

	public void setEntrainmentSequence(EntrainmentMode eMode) {
		this.eMode = eMode;
	}
}
