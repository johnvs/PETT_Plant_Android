package com.biotronisis.pettplant.communication.transfer;

import com.biotronisis.pettplant.model.ColorMode;

public class SetColorModeCommand extends AbstractCommand<EmptyResponse> {
	private static final long serialVersionUID = 1L;
	
	public static final Byte COMMAND_ID = (byte)0x35;

	private ColorMode.Mode cMode;

	@Override
	public byte[] toCommandBytes() {

		byte[] bytes = new byte[4];
		bytes[0] = 3;                        // Number of message bytes to follow
		bytes[1] = COMMAND_ID;
		bytes[2] = (byte)(cMode.getValue() + 0x20);  // Add 0x20 to translate the color mode values into command data format
                                                   // See the app's readme.md file for plant command structure
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

	public void setColorMode(ColorMode.Mode cMode) {
		this.cMode = cMode;
	}
}
