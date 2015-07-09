package com.biotronisis.pettplant.communication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Level;

//import java.math.BigInteger;
//import java.text.DecimalFormat;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import com.zlscorp.ultragrav.R;
//import com.zlscorp.ultragrav.communication.CommunicationManager.CommunicationManagerListener;
import com.zlscorp.ultragrav.debug.MyDebug;
import com.zlscorp.ultragrav.file.ErrorHandler;
import com.zlscorp.ultragrav.meter.MeterService;

public class UsbCommAdapter implements ICommAdapter {

	private static final String TAG = "UsbCommAdapter";
	
	private CommAdapterListener listener;
	
	private ConnectionState connectionState;

    private static Context meterService;
    private static D2xxManager ftdiD2xx = null;
    private FT_Device usbDevice = null;

    private int usbDeviceCount = 0;
    private boolean readThreadCancelled = false;
    private ReadThread readThread;

    private Handler uiHandler;

    public UsbCommAdapter(CommAdapterListener listener, MeterService meterService) {
		connectionState = ConnectionState.NONE;
		this.listener = listener;
		UsbCommAdapter.meterService = meterService;
        try {
            ftdiD2xx = D2xxManager.getInstance(UsbCommAdapter.meterService);
        } catch (D2xxManager.D2xxException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error getting instance of D2xxManager - ", e);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.INFO, "UsbCommAdapter.UsbCommAdapter(): " +
                    "Error getting instance of D2xxManager - " + e);
        }
        SetupD2xxLibrary();

        uiHandler = new Handler(Looper.getMainLooper());
	}

    private void SetupD2xxLibrary () {
        // Specify a non-default VID and PID combination to match if required
        if(!ftdiD2xx.setVIDPID(0x0403, 0xada1))
            if (MyDebug.LOG) {
                Log.d(TAG, "setVIDPID Error");
            }
    }

    public static synchronized boolean isUsbDeviceAttached(MeterService meter) {
        D2xxManager ftdiD2xx = null;
        int usbDeviceCount = 0;
        
        try {
            ftdiD2xx = D2xxManager.getInstance(meter);
            if (ftdiD2xx != null) {
                usbDeviceCount = ftdiD2xx.createDeviceInfoList(meter);
            }
        } catch (D2xxManager.D2xxException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error getting instance of D2xxManager - ", e);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.INFO, "UsbCommAdapter.isUsbDeviceAttached(): " +
                    "Error getting instance of D2xxManager - " + e);
        }

        return usbDeviceCount > 0;
    }
    
	@Override
	public synchronized void activate(String address) {
        if (MyDebug.LOG) {
            Log.d(TAG, "USB comm adapter activate");
        }

		// Cancel any thread currently running a connection
		if (readThread != null) {
		    readThreadCancelled = true;
		    readThread = null;
		}

	    // This is here just so if setState gets executed below, the current state is not NONE,
        // and the listener in setState gets fired.
		setState(ConnectionState.LISTENING); 

        usbDeviceCount = ftdiD2xx.createDeviceInfoList(meterService);
        if (usbDeviceCount > 0) {
            connectUsbDevice();
        } else {
            setState(ConnectionState.NONE);
            Runnable run = new Runnable() {
                public void run() {
                    Toast.makeText(meterService,
                          meterService.getString(R.string.usb_cable_not_connected),
                          Toast.LENGTH_SHORT).show();
                }
            };
            uiHandler.post(run);
        }
	}
	
	@Override
	public synchronized void deactivate() {
        if (MyDebug.LOG) {
            Log.d(TAG, "USB comm adapter deactivate");
        }

        usbDeviceCount = 0;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "InterruptedException", e);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.INFO, "UsbCommAdapter.deactivate(): " +
                    "Thread sleep interrupted - " + e);
        }

        if (usbDevice != null) {
            synchronized (usbDevice) {
                if (usbDevice.isOpen()) {
                    usbDevice.close();
                }
            }
        }

        if (readThread != null) {
            readThreadCancelled = true;
            readThread = null;
		}
		
		setState(ConnectionState.NONE);
		
		this.listener = null;
	}

    @Override
    public boolean isReConnectingToDevice(String address) {
        return false;
    }

    private boolean configureUSBDevice() {
        boolean isConfigureGood = true;
        
        // Configure comm parameters
        if (!usbDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET)) {
            isConfigureGood = false;
        }
        if (!usbDevice.setBaudRate((int) 9600)) {
            isConfigureGood = false;
        }
        if (!usbDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
                                              D2xxManager.FT_STOP_BITS_1, 
                                              D2xxManager.FT_PARITY_NONE)) {
            isConfigureGood = false;
        }
        if (!usbDevice.setFlowControl((short) D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 0x0d)) {
            isConfigureGood = false;
        }
        return isConfigureGood;
    }

    public boolean isUsbDevicePresent() {
        return usbDeviceCount > 0;
    }

//    public String getUsbDeviceName() {
//        return usbDevice.;
//    }

    public void connectUsbDevice() {
        if (usbDevice == null) {
            usbDevice = ftdiD2xx.openByIndex(meterService, 0);
        }
//            synchronized (usbDevice) {
//                usbDevice = ftdiD2xx.openByIndex(meterService, 0);
//            }
//        }

        if (usbDevice == null) {
            Runnable run = new Runnable() {
                public void run() {
                    Toast.makeText(meterService, meterService.getString(R.string.error_opening_usb_cable),
                          Toast.LENGTH_SHORT).show();
                }
            };
            uiHandler.post(run);
            return;
        }

        if (usbDevice.isOpen()) {
            configureUSBDevice();

            Runnable run = new Runnable() {
                public void run() {
                    Toast.makeText(meterService, meterService.getString(R.string.connected_to_usb_cable),
                          Toast.LENGTH_SHORT).show();
                }
            };
            uiHandler.post(run);

            if (!readThreadCancelled) {
//                Log.d(TAG, "Before stopInTask, stoppedInTask = " + usbDevice.stoppedInTask());
                usbDevice.stopInTask();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    if (MyDebug.LOG) {
                        Log.e(TAG, "thread sleep error " + e);
                    }
                    ErrorHandler errorHandler = ErrorHandler.getInstance();
                    errorHandler.logError(Level.INFO, "UsbCommAdapter.connectUsbDevice(): " +
                            "Thread sleep interrupted - " + e);
                }

                if (MyDebug.LOG) {
                    Log.d(TAG, "resetDevice = " + usbDevice.resetDevice());
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    if (MyDebug.LOG) {
                        Log.e(TAG, "thread sleep error " + e);
                    }
                    ErrorHandler errorHandler = ErrorHandler.getInstance();
                    errorHandler.logError(Level.INFO, "UsbCommAdapter.connectUsbDevice(): " +
                            "Thread sleep interrupted - " + e);
                }

                usbDevice.restartInTask();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    if (MyDebug.LOG) {
                        Log.e(TAG, "thread sleep error " + e);
                    }
                    ErrorHandler errorHandler = ErrorHandler.getInstance();
                    errorHandler.logError(Level.INFO, "UsbCommAdapter.connectUsbDevice(): " +
                            "Thread sleep interrupted - " + e);
                }

//                Log.d(TAG, "After stopInTask, stoppedInTask = " + usbDevice.stoppedInTask());
//
//                while (!usbDevice.stoppedInTask()) {
//                    Log.d(TAG, "Waiting for stoppedInTask");
//                }
//                usbDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
//                usbDevice.purge((byte) (D2xxManager.FT_PURGE_RX));
//                if (usbDevice.resetDevice()) {

//                int numBytes = usbDevice.getQueueStatus();
//                while (numBytes != 0) {
//                    if (numBytes > 0) {
//                        byte[] buffer = new byte[numBytes];
////                      if (numBytes > 0) {
//                          usbDevice.read(buffer, numBytes);
//
//                          try {
//                              Thread.sleep(50);
//                          } catch (InterruptedException e) {
//                              Log.e(TAG, "thread sleep error " + e);
//                          }
//
//                          Log.d(TAG, "usbDevice.read: numBytes = " + numBytes);
//                          for (int i = 0 ; i < numBytes ; i++) {
//                              Log.d(TAG, "usbDevice.read: buffer[" + i + "] = " + buffer[i]);
//                          }
////                      }
//                    } else {
//                        Log.d(TAG, "usbDevice.getQueueStatus error = " + numBytes);
//                    }
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, "thread sleep error " + e);
//                    }
//                    numBytes = usbDevice.getQueueStatus();
//                }

                readThread = new ReadThread();
                readThread.start();

                setState(ConnectionState.ESTABLISHED);

//                } else {
//                    Log.w(TAG, "Failed to reset USB device");
//                }
            }
        } else {
            Runnable run = new Runnable() {
                public void run() {
                    Toast.makeText(meterService, meterService.getString(R.string.error_opening_usb_cable),
                          Toast.LENGTH_SHORT).show();
                }
            };
            uiHandler.post(run);
        }
    }

    @Override
	public void sendBytes(byte[] command) {
        if (!usbDevice.isOpen()) {
            if (MyDebug.LOG) {
                Log.e(TAG, "SendMessage: device not open");
            }
            return;
        }

        usbDevice.setLatencyTimer((byte) 16);

        if (MyDebug.LOG) {
            Log.d("EndObsRaceCond", "Sending Command to meter: " + String.format("%05X", command[1]));
        }
        int numBytesWritten = usbDevice.write(command);
        
        if (numBytesWritten != command.length) {
            if (MyDebug.LOG) {
                Log.d(TAG, "SendMessage: not all bytes sent: " + command.length + " vs " + numBytesWritten);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.INFO, "UsbCommAdapter.sendBytes(): " +
                    "Not all bytes sent - " + numBytesWritten + " vs " + command.length);
        }
    }

    @Override
	public ConnectionState getConnectionState() {
		return connectionState;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(ConnectionState newState) {
		ConnectionState oldState = connectionState;
		connectionState = newState;
		
        if (MyDebug.LOG) {
            Log.d(TAG, "setState() " + oldState + " -> " + newState);
        }
		
		if (newState != oldState && listener != null) {
			listener.onConnectionState(newState);
		}
	}

    /**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
//	private void connectionFailed() {
//		// Send a failure message back to the Activity
//		Log.w(TAG, "connection failed");
//		setState(ConnectionState.NONE);
//	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
//	private void connectionLost() {
//		// Send a failure message back to the Activity
//		Log.w(TAG, "connection lost");
//		setState(ConnectionState.NONE);
//	}


	/**
	 * This thread runs during a connection with the USB cable/meter. It handles all incoming and outgoing transmissions.
	 */
    private class ReadThread extends Thread {

        ReadThread() {
            this.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            if (MyDebug.LOG) {
                Log.d(TAG, "BEGIN USB ReadThread");
            }
            byte[] buffer = new byte[1024];
            int numBytes;

            while (!readThreadCancelled) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

                synchronized (usbDevice) {
                    numBytes = usbDevice.getQueueStatus();
                    if (numBytes > 0) {
                        // Delay to make sure the whole message has been received
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                        }

                        // Then read the queue status again, in case more bytes have arrived 
                        numBytes = usbDevice.getQueueStatus();

                        usbDevice.read(buffer, numBytes);

                        byte[] response = new byte[numBytes];
                        System.arraycopy(buffer, 0, response, 0, numBytes);

                        if (MyDebug.LOG) {
                            StringBuilder responseBytesStr = new StringBuilder();
                            if (response.length > 0) {
                                for (byte b : response) {
                                    responseBytesStr.append(String.format("%02X ", b));
                                }       
                            }
                            Log.d(TAG, "responseBytes= " + responseBytesStr.toString());
                        }
                        
                        if (listener != null) {
                            listener.onReceiveBytes(response);
                        }
                    }
                }
            }
        }
    }
}
