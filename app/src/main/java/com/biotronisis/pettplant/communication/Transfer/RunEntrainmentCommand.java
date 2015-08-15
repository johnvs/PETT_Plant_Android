package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.model.Entrainment;

public class RunEntrainmentCommand extends AbstractCommand<RunEntrainmentResponse> {
	private static final long serialVersionUID = 1L;
	
	public static final Byte COMMAND_ID = (byte)0x10;
	
	private Entrainment.Sequence sequence;
	
	@Override
	public byte[] toCommandBytes() {

		byte[] bytes = new byte[4];
		bytes[0] = 3;                        // Number of message bytes to follow
		bytes[1] = COMMAND_ID;
		bytes[2] = (byte)(sequence.getValue() + 0x01); // Add 0x01 to translate the sequence numbers into command data format
		                                            // See the app's readme.md file for plant command structure
		bytes[3] = computeChecksum(bytes);
		
		return bytes;
	}

	@Override
	public Byte getCommandId() {
		return COMMAND_ID;
	}

	@Override
	public Class<RunEntrainmentResponse> getResponseClass() {
		return RunEntrainmentResponse.class;
	}
	
	@Override
	public Byte getResponseId() {
		return RunEntrainmentResponse.RESPONSE_ID;
	}

	public void setEntrainmentSequence(Entrainment.Sequence seq) {
		this.sequence = seq;
	}
}
