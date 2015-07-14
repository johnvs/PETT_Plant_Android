/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biotronisis.pettplant.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import android.util.Log;
//import com.biotronisis.common.logger.Log;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.service.PettPlantService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothCommAdapter implements ICommAdapter {
    private static final String TAG = "BluetoothCommAdapter";

    // Name for the SDP record when creating server socket
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application used for SPP profile
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

   private CommAdapterListener listener;

   private final BluetoothAdapter bluetoothAdapter;
//   private final Handler mHandler;
   private AcceptThread mInsecureAcceptThread;
   private ConnectThread connectThread;
   private ConnectedThread connectedThread;
   private ConnectionState connectionState;
   private MyBluetoothBroadcastReceiver bluetoothReceiver;
   private MyBluetoothBroadcastReceiver resetReceiver;
   private MyBluetoothBroadcastReceiver enableReceiver;
   private Context meterService;
   private String currentDeviceAddress;
   private boolean isActivating;
   private boolean isReConnecting;
   private boolean isResetting;
   private boolean isEnabling;
   private Boolean waitingForBTState;
   private Handler backgroundHandler;

    // Constants that indicate the current connection state
//    public static final int STATE_NONE       = 0;    // we're doing nothing
//    public static final int STATE_LISTEN     = 1;    // now listening for incoming connections
//    public static final int STATE_CONNECTING = 2;    // now initiating an outgoing connection
//    public static final int STATE_CONNECTED  = 3;    // now connected to a remote device

    // BluetoothCommAdapter Handler message types
    public static final int MESSAGE_STATE_CHANGE  = 1;
    public static final int MESSAGE_READ          = 2;
    public static final int MESSAGE_WRITE         = 3;
    public static final int MESSAGE_DEVICE_NAME   = 4;
    public static final int MESSAGE_TOAST         = 5;

    // Key names received from the BluetoothCommAdapter Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param listener
     * @param pettPlantService
     */
//    public BluetoothCommAdapter(Context context, Handler handler) {
//    public BluetoothCommAdapter(Handler handler) {
    public BluetoothCommAdapter(CommAdapterListener listener, PettPlantService pettPlantService) {
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       connectionState = ConnectionState.NONE;
       this.listener = listener;
       this.meterService = meterService;
       isReConnecting = false;
       isResetting = false;

       HandlerThread backgroundThread = new HandlerThread("meter-service");
       backgroundThread.start();
       this.backgroundHandler = new Handler(backgroundThread.getLooper());
//       mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param newState An integer defining the current connection state
     */
    private synchronized void setState(ConnectionState newState) {
//        Log.d(TAG, "setState() " + connectionState + " -> " + state);
//        connectionState = state;
//
//        // Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state.getId(), -1).sendToTarget();

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
     * Return the current connection state.
     */
    public synchronized ConnectionState getState() {
        return connectionState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    @Override
    public synchronized void activate(String address) {
       if (MyDebug.LOG) {
          Log.d(TAG, "bluetooth comm adapter activate");
       }

       currentDeviceAddress = address;

       // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(ConnectionState.LISTENING);

        // Start the thread to listen on a BluetoothServerSocket
//        if (mSecureAcceptThread == null) {
//            mSecureAcceptThread = new AcceptThread(true);
//            mSecureAcceptThread.start();
//        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (connectionState == ConnectionState.CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(ConnectionState.CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
       if (MyDebug.LOG) {
          Log.d(TAG, "connected, Socket Type:" + socketType);
       }

       // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

       // Send the name of the connected device back to the UI Activity
       if (MyDebug.LOG) {
          Log.d(TAG, "connection established");
       }

       setState(ConnectionState.ESTABLISHED);
    }

   @Override
   public ConnectionState getConnectionState() {
      return connectionState;
   }

   /**
     * Stop all threads
     */
    @Override
    public synchronized void deactivate() {
        Log.d(TAG, "deactivate");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }


        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(ConnectionState.NONE);
    }

   @Override
   public void sendBytes(byte[] command) {
      // Create temporary object
      ConnectedThread r;
      // Synchronize a copy of the ConnectedThread
      synchronized (this) {
         if (connectionState != ConnectionState.ESTABLISHED) {
            if (MyDebug.LOG) {
               Log.e(TAG, "cannot write to a connection that is not established");
            }
            return;
         }
         r = connectedThread;
      }
      // Perform the write unsynchronized

      //String msg = "ULTRAGRAV_TEST";
      //r.write(msg.getBytes());

      r.write(command);

   }

   @Override
   public boolean isReConnectingToDevice(String address) {
      return isReConnecting && address.equals(currentDeviceAddress);
   }

   private void doDiscovery() {
      // Request discover from BluetoothAdapter
      if (MyDebug.LOG) {
         Log.i(TAG, "Discovery Started");
      }
      bluetoothAdapter.startDiscovery();
   }

   /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (connectionState != ConnectionState.ESTABLISHED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
//
//        // Start the service over to restart listening mode
//        BluetoothCommAdapter.this.start();

       // Send a failure message back to the Activity
       if (MyDebug.LOG) {
          Log.w(TAG, "connection failed");
       }
       setState(ConnectionState.FAILED);

    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
//
//        // Start the service over to restart listening mode
//        BluetoothCommAdapter.this.start();

       // Send a failure message back to the Activity
       if (MyDebug.LOG) {
          Log.w(TAG, "connection lost");
       }
       setState(ConnectionState.NONE);

       // Attempt to reconnect with the same BT device
       reConnect();

    }

   /**
    * Auto-reconnect
    * If the BT connection is lost, try to reconnect
    * This procedure is cancelled in the following cases:
    * 1 - A connection is reestablished
    * 2 - The user initiates a connection with a different BT device
    * 3 - A USB connection is established
    */
   private void reConnect() {

      bluetoothReceiver = new MyBluetoothBroadcastReceiver();

      // Register for broadcasts when a device is discovered
      IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
      meterService.registerReceiver(bluetoothReceiver, filter);

      // Register for broadcasts when discovery has finished
      filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      meterService.registerReceiver(bluetoothReceiver, filter);

      connectedThread = null;
      isActivating = false;
      isReConnecting = true;

      doDiscovery();
   }

   /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                if (!secure) {
                    tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (connectionState != ConnectionState.ESTABLISHED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothCommAdapter.this) {
                        switch (connectionState) {
                            case LISTENING:
                            case CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case NONE:
                            case ESTABLISHED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (!secure) {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                          MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothCommAdapter.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
       private final BluetoothSocket mmSocket;
       private final InputStream mmInStream;
       private final OutputStream mmOutStream;
       private boolean cancelled = false;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
//            Log.d(TAG, "create ConnectedThread: " + socketType);
           if (MyDebug.LOG) {
              Log.d(TAG, "create ConnectedThread: " + socketType);
           }
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
//                Log.e(TAG, "temp sockets not created", e);

               if (MyDebug.LOG) {
                  Log.e(TAG, "temp sockets not created", e);
               }
               ErrorHandler errorHandler = ErrorHandler.getInstance();
               errorHandler.logError(Level.WARNING, "BluetoothCommAdapter$ConnectedThread." +
                           "ConnectedThread(): Could not get socket stream - " + e,
                     R.string.bluetooth_adapter_error_title,
                     R.string.bluetooth_adapter_error_message);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
           if (MyDebug.LOG) {
              Log.i(TAG, "BEGIN mConnectedThread");
           }
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (!cancelled) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

//                    // Send the obtained bytes to the UI Activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    Log.e(TAG, "disconnected", e);
//                    connectionLost();
//                    // Start the service over to restart listening mode
//                    BluetoothCommAdapter.this.start();
//                    break;
//                }

                   byte[] response = new byte[bytes];
                   System.arraycopy(buffer, 0, response, 0, bytes);

                   if (listener != null) {
                      listener.onReceiveBytes(response);
                   }
                } catch (IOException e) {
                   if (!cancelled) {
                      if (MyDebug.LOG) {
                         Log.e(TAG, "connected thread read error", e);
                      }
                      connectionLost();
                      cancel();
                   }
                   break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
//                Log.e(TAG, "Exception during write", e);

               if (!cancelled) {
                  if (MyDebug.LOG) {
                     Log.e(TAG, "connected thread write error", e);
                  }
                  connectionLost();
               }
            }
        }

        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "close() of connect socket failed", e);
//            }

           cancelled = true;
           try {
              mmSocket.close();
           } catch (IOException e) {
              if (MyDebug.LOG) {
                 Log.e(TAG, "close() of connect socket failed", e);
              }
              ErrorHandler errorHandler = ErrorHandler.getInstance();
              errorHandler.logError(Level.WARNING, "BluetoothCommAdapter$ConnectedThread." +
                          "cancel(): Could not close the socket - " + e,
                    R.string.bluetooth_adapter_error_title,
                    R.string.bluetooth_adapter_error_message);
           }
        }
    }

   // The BroadcastReceiver that listens for discovered devices and
   // tries to connect if the most recent BT device is found
   private class MyBluetoothBroadcastReceiver extends BroadcastReceiver {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();

         // When discovery finds a device
         if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it's the device we were most recently connected to, reconnect with it
            if (device.getAddress().equals(currentDeviceAddress)) {
               if (MyDebug.LOG) {
                  Log.i(TAG, "Found previous BT device; trying to reactivate");
               }
               isActivating = true;
               activate(currentDeviceAddress);
            }
            // When discovery is finished, if we have not yet reconnected, continue trying
         } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (MyDebug.LOG) {
               Log.i(TAG, "Discovery Finished.");
            }
            if (isActivating) {
               // Unregister broadcast listener
               meterService.unregisterReceiver(bluetoothReceiver);
               isReConnecting = false;
            } else if (isReConnecting) {
               doDiscovery();
            }
         } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            synchronized (waitingForBTState) {
               if (MyDebug.LOG) {
                  Log.i(TAG, "Bluetooth Adapter State has changed. isEnabling: " + isEnabling + ", " +
                        "isResetting: " + isResetting + ", BT State: " + bluetoothAdapter.getState());
               }
               if (isEnabling) {
                  if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                     if (MyDebug.LOG) {
                        Log.i(TAG, "BroadcastReceiver - Enabling BT Adapter - State is ON.");
                     }
                     waitingForBTState = false;
                     // Unregister broadcast listener
                     meterService.unregisterReceiver(enableReceiver);
                  }
               } else if (isResetting) {
                  if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                     if (MyDebug.LOG) {
                        Log.i(TAG, "BroadcastReceiver - Resetting BT Adapter - State is OFF.");
                     }
                     waitingForBTState = false;
                  } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                     if (MyDebug.LOG) {
                        Log.i(TAG, "BroadcastReceiver - Resetting BT Adapter - State is ON.");
                     }
                     waitingForBTState = false;
                     // Unregister broadcast listener
                     meterService.unregisterReceiver(resetReceiver);
                  }
               }
            }
         }
      }
   }

}
