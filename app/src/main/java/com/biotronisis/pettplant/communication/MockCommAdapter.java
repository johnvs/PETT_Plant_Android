package com.biotronisis.pettplant.communication;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

//import com.zlscorp.ultragrav.communication.transfer.AlternateBreakCommand;
//import com.zlscorp.ultragrav.communication.transfer.BeginIntervalReadingCommand;
//import com.zlscorp.ultragrav.communication.transfer.EndIntervalReadingCommand;
//import com.zlscorp.ultragrav.communication.transfer.GetPwmDutyCycleCommand;
import com.biotronisis.pettplant.communication.transfer.RequestStateCommand;
import com.biotronisis.pettplant.debug.MyDebug;

public class MockCommAdapter implements ICommAdapter {

    private static final String TAG = "MockCommAdapter";

    private final Handler commHandler;
    private final CommAdapterListener listener;
    private boolean active = false;

    private ConnectionState connectionState = ConnectionState.NONE;

    MockCommAdapter(CommAdapterListener listener) {
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

        commHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                connectionState = ConnectionState.NONE;
                listener.onConnectionState(connectionState);
            }
        }, 1000);
    }

    @Override
    public void connLost() {
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

        if (command[1] == RequestStateCommand.COMMAND_ID) {
            commHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    byte[] bytes = new byte[]{8, (byte) 0xC0, 0, 0, 0, 0, 0, 50, 0};
                    bytes[8] = checksum(bytes);
                    listener.onReceiveBytes(bytes);
                }
            }, 500);
        }
    }

    @Override
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    private byte checksum(byte[] bytes) {
        byte checksum = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            checksum ^= bytes[i];
        }
        return checksum;
    }

}
