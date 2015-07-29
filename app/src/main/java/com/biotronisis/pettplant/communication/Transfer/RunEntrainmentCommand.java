package com.biotronisis.pettplant.communication.transfer;

public class RunEntrainmentCommand extends AbstractCommand<EmptyResponse> {
	private static final long serialVersionUID = 1L;
	
	public static final Byte COMMAND_ID = (byte)0x02;
	
	private int dutyCycle;
	
	@Override
	public byte[] toCommandBytes() {
		byte msb = asMsb(dutyCycle);
		byte lsb = asLsb(dutyCycle);
		
		byte[] bytes = new byte[5];
		bytes[0] = 4;
		bytes[1] = COMMAND_ID;
		bytes[2] = msb;
		bytes[3] = lsb;
		bytes[4] = computeChecksum(bytes);
		
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

	public int getDutyCycle() {
		return dutyCycle;
	}

	public void setDutyCycle(int dutyCycle) {
		this.dutyCycle = dutyCycle;
	}
}
