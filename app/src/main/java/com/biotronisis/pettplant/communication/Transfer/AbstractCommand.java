package com.biotronisis.pettplant.communication.transfer;

//import com.biotronisis.pettplant.file.ErrorHandler;

import java.io.Serializable;

public abstract class AbstractCommand<R extends AbstractResponse> implements Serializable {

    private final String TAG = "AbstractCommand";
    
	private static final long serialVersionUID = 1L;
	
	// the number of milliseconds to wait for a response before handling a timeout
	private static final long RESPONSE_TIMEOUT = 4000;     // was 5000
	
	private ResponseCallback<R> responseCallback;
	public abstract byte[] toCommandBytes();
	public abstract Byte getCommandId();
	
	public abstract Byte getResponseId();
	public abstract Class<R> getResponseClass();
	
//	public Byte getIntervalEndCommandId() {
//		return null;
//	}
	
//	public boolean isInterval() {
//		return getIntervalEndCommandId() != null;
//	}
	
	public long getResponseTimeoutMs() {
		return RESPONSE_TIMEOUT;
	}
	
	public byte asLsb(int number) {
		return (byte)(number & 0x000000FF);
	}
	
	public byte asMsb(int number) {
		return (byte)((number & 0x0000FF00) >> 8);
	}
	
/*
	public byte computeChecksum(byte[] bytes) {

        StringBuilder bytesStrBldr = new StringBuilder();
        if (bytes.length > 0) {
            for (byte b : bytes) {
                bytesStrBldr.append(String.format("%02X ", b));
            }       
        }
        String byteStr = bytesStrBldr.toString();
                
        if (MyDebug.LOG) {
            Log.d(TAG, "computeChecksum bytes = " + byteStr);
        }
	    
		if (bytes[0] < 2 || bytes[0]  != bytes.length-1) {
//		    ErrorHandler errorHandler = ErrorHandler.getInstance();
//	        errorHandler.logError(Level.SEVERE, this.getClass().getSimpleName() + ".computeChecksum(): " +
//	        		"Command bytes is too short: " + byteStr, 0, 0);
		}
		
		byte checksum = 0;
		for (int i=0; i<bytes.length-1; i++) {
			checksum ^= bytes[i];
		}
		
		return checksum;
	}
*/

   public void setResponseCallback(ResponseCallback<R> responseCallback) {
      this.responseCallback = responseCallback;
   }
   public ResponseCallback<R> getResponseCallback() {
      return responseCallback;
   }
}
