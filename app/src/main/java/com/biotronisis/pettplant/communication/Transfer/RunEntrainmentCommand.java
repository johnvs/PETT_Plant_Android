package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.type.EntrainmentMode;

public class RunEntrainmentCommand extends AbstractCommand<EmptyResponse> {
	private static final long serialVersionUID = 1L;
	
	public static final Byte COMMAND_ID = (byte)0x02;
	
	private EntrainmentMode eMode;
	
	@Override
	public byte[] toCommandBytes() {

		byte[] bytes = new byte[3];
		bytes[0] = 4;
		bytes[1] = COMMAND_ID;
		bytes[2] = (byte)eMode.getValue();
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

//	public int getDutyCycle() {
//		return dutyCycle;
//	}

	public void setEntrainmentSequence(EntrainmentMode eMode) {
		this.eMode = eMode;
	}
}
