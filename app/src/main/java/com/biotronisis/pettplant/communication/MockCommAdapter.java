package com.biotronisis.pettplant.communication;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

//import com.zlscorp.ultragrav.communication.transfer.AlternateBreakCommand;
//import com.zlscorp.ultragrav.communication.transfer.BeginIntervalReadingCommand;
//import com.zlscorp.ultragrav.communication.transfer.EndIntervalReadingCommand;
//import com.zlscorp.ultragrav.communication.transfer.GetPwmDutyCycleCommand;
import com.biotronisis.pettplant.debug.MyDebug;

public class MockCommAdapter implements ICommAdapter {
	
	private static final String TAG = "MockCommAdapter";
	
	private Handler commHandler;
	private CommAdapterListener listener;
	private boolean active = false;
	
	private ConnectionState connectionState = ConnectionState.NONE;
	
	private Runnable datasetRunnable;
	
	public MockCommAdapter(CommAdapterListener listener) {
		this.listener = listener;
		
		HandlerThread commThread = new HandlerThread("mock-commThread");
		commThread.start();
		commHandler = new Handler(commThread.getLooper());
	}
	 
	@Override
	public synchronized void activate(String address) {
		
		commHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				connectionState = ConnectionState.CONNECTING;
				listener.onConnectionState(connectionState);
			}
		}, 100);
		
		commHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				connectionState = ConnectionState.ESTABLISHED;
				listener.onConnectionState(connectionState);
			}
		}, 2000);
		active = true;
	}

	@Override
	public synchronized void deactivate() {
		active = false;
		if (datasetRunnable != null) {
			commHandler.removeCallbacks(datasetRunnable);
			datasetRunnable = null;
		}
		
		commHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				connectionState = ConnectionState.NONE;
				listener.onConnectionState(connectionState);
			}
		}, 1000);
	}
	
    @Override
    public boolean isReConnectingToDevice(String address) {
        return false;
    }

    @Override
	public void sendBytes(byte[] command) {
        if (MyDebug.LOG) {
            Log.d(TAG, "bytes sent to mock comm adapter.");
        }
		
		if (!active) {
			new RuntimeException("connection not active");
		}
		
/*
		if (command[1] == BeginIntervalReadingCommand.COMMAND_ID) {
			datasetRunnable = new DatasetRunnable();
			commHandler.postDelayed(datasetRunnable, 1000);
	        if (MyDebug.LOG) {
	            Log.e(TAG, "started timer for mock 0x01");
	        }
		} else if (command[1] == EndIntervalReadingCommand.COMMAND_ID) {
			if (datasetRunnable!=null) {
				commHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						commHandler.removeCallbacks(datasetRunnable);
						datasetRunnable = null;
					}
				}, 500);
			}
		} else if (command[1] == AlternateBreakCommand.COMMAND_ID) {
			commHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					byte[] bytes = new byte[]{0x02, 0x07, 0x00};     // Should be 2
					bytes[2] = checksum(bytes);
					listener.onReceiveBytes(bytes);
				}
			}, 500);
		} else if (command[1] == GetPwmDutyCycleCommand.COMMAND_ID) {
            commHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    byte[] bytes = new byte[]{0x04, (byte) 0xfe, (byte) 0x80, (byte) 0x00, 0x00};       // 0x8000 = 50%
                    bytes[4] = checksum(bytes);
                    listener.onReceiveBytes(bytes);
                }
            }, 500);
		    
		}
*/
	}
	
	@Override
	public ConnectionState getConnectionState() {
		return connectionState;
	}
	
	private byte checksum(byte[] bytes) {
		byte checksum = 0;
		for (int i=0; i<bytes.length-1; i++) {
			checksum ^= bytes[i];
		}
		return checksum;
	}
	
	private class DatasetRunnable implements Runnable {
		
		private int[] beam = {
				0x00,0x00,
				0x3F,0x7A,
				0x7E,0xF4,
				0xBE,0x6E,
				0xFD,0xE8,
				0x3D,0x63,
				0x7C,0xDD,
				0xBC,0x57,
				0xFB,0xD1,
				0x3B,0x4C,
				0x7A,0xC6,
				0xBA,0x40,
				0xF9,0xBA,
				0x39,0x35,
				0x78,0xAF,
				0xB8,0x29,
				0xF7,0xA3,
				0x37,0x1E,
				0x76,0x98,
				0xB6,0x12,
				0xF5,0x8C
		};
		
		private int[] longLevel = {
				0x00,0x00,
				0x55,0xF0,
				0xAB,0xE0,
				0x01,0xD1,
				0x57,0xC1,
				0xAD,0xB1,
				0x03,0xA2,
				0x59,0x92,
				0xAF,0x82,
				0x05,0x73,
				0x5B,0x63,
				0xB1,0x53,
				0x07,0x44,
				0x5D,0x34,
				0xB3,0x24,
				0x09,0x15,
				0x5F,0x05,
				0xB4,0xF5,
				0x0A,0xE6,
				0x60,0xD6,
				0xB6,0xC6
		};
		
		private int[] crossLevel = {
				0x00,0x00,
				0x59,0xD8,
				0xB3,0xB0,
				0x0D,0x89,
				0x67,0x61,
				0xC1,0x39,
				0x1B,0x12,
				0x74,0xEA,
				0xCE,0xC2,
				0x28,0x9B,
				0x82,0x73,
				0xDC,0x4B,
				0x36,0x24,
				0x8F,0xFC,
				0xE9,0xD4,
				0x43,0xAD,
				0x9D,0x85,
				0xF7,0x5D,
				0x51,0x36,
				0xAB,0x0E,
				0x04,0xE7
		};
		
		private int index = 0;

		@Override
		public void run() {
	        if (MyDebug.LOG) {
	            Log.d(TAG, "mock bytes create");
	        }
			
			commHandler.postDelayed(this, 1000);
			
			int x = index;
			int y = index+1;
			
			index += 2;
			if (index >= beam.length) {
				index = 0;
			}
			
			byte[] bytes = new byte[]
					{9, 0x03, (byte)beam[x],(byte)beam[y],(byte)longLevel[x],(byte)longLevel[y],
			        (byte)crossLevel[x],(byte)crossLevel[y],(byte)0x80, 0}; // First value was 10
			
			bytes[9] = checksum(bytes);
			
			listener.onReceiveBytes(bytes);
		}
	}
}
