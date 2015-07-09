package com.biotronisis.pettplant.communication.transfer;

import com.zlscorp.ultragrav.file.ErrorHandler;

import java.util.logging.Level;

public class EmptyResponse extends AbstractResponse {
	private static final long serialVersionUID = 1L;
	
    public static final Byte RESPONSE_ID = null;
	public static final int MIN_RESPONSE_LENGTH = 0;

	@Override
	public Byte getResponseId() {
		return RESPONSE_ID;
	}
	
	@Override
	public int getMinimumResponseLength() {
		return 0;
	}

	@Override
	public void fromResponseBytes(byte[] responseBytes) {
	    ErrorHandler errorHandler = ErrorHandler.getInstance();
        errorHandler.logError(Level.INFO, "EmptyResponse.fromResponseBytes(): " +
        		"Can not create an empty response from bytes", 0, 0);
	}
}
