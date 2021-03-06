package com.biotronisis.pettplant.communication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.communication.ICommAdapter.CommAdapterListener;
import com.biotronisis.pettplant.communication.transfer.AbstractCommand;
import com.biotronisis.pettplant.communication.transfer.AbstractResponse;
import com.biotronisis.pettplant.communication.transfer.EmptyResponse;
import com.biotronisis.pettplant.communication.transfer.ResponseCallback;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.plant.PettPlantService;
import com.biotronisis.pettplant.type.CommunicationType;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.view.WindowManager;

/**
 * Establish and manage communications with the Burris gravity meter via bluetooth or USB.
 */
public class CommunicationManager {

    private static final String TAG = "CommunicationManager";

    private final PettPlantService pettPlantService;

    private final CommunicationParams communicationParams;

    // handles sending and receiving bytes over some channel
    private ICommAdapter commAdapter;

    // a general purpose handler to ensure a Runnable is run on the background thread
    private final Handler backgroundHandler;

    // a general handler for running code on the ui thread
    private final Handler uiHandler;

    // mapping response id and command id to waiting command
    private final Map<Byte, AbstractCommand<?>> responseIdToWaitingCommands;
    private final Map<Byte, AbstractCommand<?>> commandIdToWaitingCommands;

    // the timeout for the currently waiting command that is waiting a response
    private final Map<AbstractCommand<?>, TimeoutBackgroundRunnable> waitingCommandsToTimeouts;

    // maps the end interval command id to the waitingCommand id
//   private Map<Byte, Byte> endCommandIdToStartCommandId;

//   private boolean justEndedInterval;

    @SuppressLint("UseSparseArrays")
    public CommunicationManager(PettPlantService pettPlantService, CommunicationParams communicationParams) {

        this.pettPlantService = pettPlantService;
        this.communicationParams = communicationParams;

        HandlerThread backgroundThread = new HandlerThread("pett-plant-service");
        backgroundThread.start();
        this.backgroundHandler = new Handler(backgroundThread.getLooper());

        this.uiHandler = new Handler(Looper.getMainLooper());

        this.responseIdToWaitingCommands = new HashMap<>();
        this.commandIdToWaitingCommands = new HashMap<>();
        this.waitingCommandsToTimeouts = new HashMap<>();
//      this.endCommandIdToStartCommandId = new HashMap<Byte, Byte>();

        // the callback used by the current commAdapter to notify this of responses
        CommAdapterListener commResponseListener = new MyCommAdapterListener();

        // Test
//		switch (CommunicationType.TEST) {
        switch (communicationParams.getCommunicationType()) {
            case MOCK:
                this.commAdapter = new MockCommAdapter(commResponseListener);
                break;
            case BLUETOOTH:
                this.commAdapter = new BluetoothCommAdapter(commResponseListener, this.pettPlantService);
                break;
//			case USB:
//				this.commAdapter = new UsbCommAdapter(commResponseListener, this.pettPlantService);
//				break;
            default:
                ErrorHandler errorHandler = ErrorHandler.getInstance(this.pettPlantService);
                errorHandler.logError(Level.WARNING, "CommunicationManager.CommunicationManager(): " +
                            "unknown CommunicationType - " + communicationParams.getCommunicationType(),
                      R.string.communication_type_error_title,
                      R.string.communication_type_error_message);
        }
    }

    public boolean isReConnectingToBtDevice(String address) {
        return (communicationParams.getCommunicationType() == CommunicationType.BLUETOOTH &&
              commAdapter.isReConnectingToDevice(address));
    }

//    public boolean isUsbDeviceAttached(PettPlantService meter) {
//        return UsbCommAdapter.isUsbDeviceAttached(meter);
//    }

    public void connect() {            // throws Exception {
        if (MyDebug.LOG) {
            Log.d(TAG, "connect. name=" + communicationParams.getName() + " address:" +
                  communicationParams.getAddress());
        }
        if (commAdapter == null) {
            ErrorHandler errorHandler = ErrorHandler.getInstance(pettPlantService);
            errorHandler.logError(Level.SEVERE, "CommunicationManager.connect(): " +
                        "commAdapter does not exist.",
                  R.string.bluetooth_adapter_error_title,
                  R.string.bluetooth_adapter_error_message);
        } else {
            commAdapter.activate(communicationParams.getAddress());

            ErrorHandler errorHandler = ErrorHandler.getInstance();
            if (errorHandler != null) {
                errorHandler.logError(Level.INFO, "CommunicationManager.connect():" +
                      " executing commAdapter.activate(" + communicationParams.getAddress() + ").", 0, 0);
            } else {
                if (MyDebug.LOG) {
                    Log.d(TAG, "errorHandler is null.");
                }
            }

        }
    }

    public void connectionLost() {
        commAdapter.connLost();
    }

    public void disconnect() {
        commAdapter.deactivate();
    }

    /**
     * Returns the current CommAdapter being used for sending data to the meter
     */
    public ICommAdapter getCurrentCommAdapter() {
        return commAdapter;
    }

    /**
     * Sends a Command to the meter and will callback the ResponseCallback when a response
     * is received or on failure. ResponseCallback should be null for commands with no response.
     * NOTE: Do not reuse Command objects.
     * NOTE: This call is async and returns before the command is actually sent
     */
    public void sendCommand(final AbstractCommand<? extends AbstractResponse> command) {

        // Test
//	    if (command.getCommandId() == EndIntervalReadingCommand.COMMAND_ID) {
//	      backgroundHandler = null;
//	    }

        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                sendCommandBackground(command);
            }
        });
    }

    private void sendCommandBackground(AbstractCommand<? extends AbstractResponse> command) {

        ErrorHandler errorHandler = ErrorHandler.getInstance();
        if (errorHandler != null) {
            errorHandler.logError(Level.INFO, "CommunicationManager.sendCommandBackground():" +
                  " sent command - " + command.getCommandId(), 0, 0);
        } else {
            if (MyDebug.LOG) {
                Log.d(TAG, "errorHandler is null.");
            }
        }

        Byte commandId = command.getCommandId();
        if (commandIdToWaitingCommands.containsKey(commandId)) {
            pettPlantService.dispatchCommError(CommunicationErrorType.TRANSMIT_FAILED,
                  "a command with this same command id is already in progress", commandId);
            return;
        }
        Byte responseId = command.getResponseId();
        if (responseId != null && responseIdToWaitingCommands.containsKey(responseId)) {
            pettPlantService.dispatchCommError(CommunicationErrorType.TRANSMIT_FAILED,
                  "a command with this same response id is already in progress", commandId);
            return;
        }

//      ErrorHandler errorHandler = ErrorHandler.getInstance();
//      if (errorHandler != null) {
//         errorHandler.logError(Level.INFO, "CommunicationManager.sendCommandBackground():" +
//               " command ID - " + commandId, 0, 0);
//      } else {
//         if (MyDebug.LOG) {
//            Log.d(TAG, "errorHandler is null.");
//         }
//      }

        ICommAdapter adapter = getCurrentCommAdapter();
        byte[] bytes = command.toCommandBytes();
        if (MyDebug.LOG) {
            Log.d(TAG, "sendBytes=" + new BigInteger(bytes).toString(16));
        }
        adapter.sendBytes(bytes);

        // check if this command ends an interval
//      if (endCommandIdToStartCommandId.containsKey(commandId)) {
//
//         Byte startCommandId = endCommandIdToStartCommandId.remove(commandId);
//         AbstractCommand<?> startCommand = commandIdToWaitingCommands.remove(startCommandId);
//
//         if (MyDebug.LOG) {
//            Log.d("EndObsRaceCond", "Unregistering Interval command");
//         }
//         responseIdToWaitingCommands.remove(startCommand.getResponseId());
//
//         // cancel timeout for interval command
//         TimeoutBackgroundRunnable timeout = waitingCommandsToTimeouts.remove(startCommand);
//         backgroundHandler.removeCallbacks(timeout);
//         justEndedInterval = true;
//      }

        ResponseCallback<? extends AbstractResponse> callback = command.getResponseCallback();
        if (callback != null) {

            if (responseId == null) {
                // not looking for a response id, dispatch an empty response to be sent
                uiHandler.post(new ResponseCallbackUiRunnable(callback, new EmptyResponse()));
            } else {

                commandIdToWaitingCommands.put(commandId, command);
                responseIdToWaitingCommands.put(responseId, command);

                // check if this command starts an interval
//				if (command.isInterval()) {
//					endCommandIdToStartCommandId.put(command.getIntervalEndCommandId(), commandId);
//					justEndedInterval = false;
//				}

                TimeoutBackgroundRunnable timeout = new TimeoutBackgroundRunnable(command);
                waitingCommandsToTimeouts.put(command, timeout);
                backgroundHandler.postDelayed(timeout, command.getResponseTimeoutMs());
            }
        }

        pettPlantService.dispatchCommSent();
    }

    private void handleResponseBytes(final byte[] responseBytes) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponseBytesBackground(responseBytes);
            }
        });
    }

    private void handleResponseBytesBackground(byte[] responseBytes) {
        StringBuilder responseBytesStr = new StringBuilder();
        if (responseBytes.length > 0) {
            for (byte b : responseBytes) {
                responseBytesStr.append(String.format("%02X ", b));
            }
        }
        if (MyDebug.LOG) {
            Log.d(TAG, "responseBytes= " + responseBytesStr.toString());
//            Log.d(TAG, "responseBytes=" + new BigInteger(responseBytes).toString(16));
        }

        ErrorHandler errorHandler = ErrorHandler.getInstance();
        if (errorHandler != null) {
            errorHandler.logError(Level.INFO, "CommunicationManager.handleResponseBytesBackground():" +
                  " received bytes - " + responseBytesStr, 0, 0);
        } else {
            if (MyDebug.LOG) {
                Log.d(TAG, "errorHandler is null.");
            }
        }

        if (responseBytes.length < 3) {
            pettPlantService.dispatchCommError(CommunicationErrorType.MALFORMED_RESPONSE,
                  "response length too short", null);
            return;
        }

        byte msgLength = responseBytes[0];
        if (responseBytes.length < msgLength) {
            pettPlantService.dispatchCommError(CommunicationErrorType.MALFORMED_RESPONSE,
                  "response length too short", null);
            return;
        }

        Byte responseId = responseBytes[1];
        AbstractCommand<?> waitingCommand = responseIdToWaitingCommands.get(responseId);

        if (waitingCommand == null) {
            pettPlantService.dispatchCommError(CommunicationErrorType.UNEXPECTED_RESPONSE,
                  "received response but none requested", responseId);

            // can't call onFailed on a callback, because we don't know who this belongs to
            return;
        }

        AbstractResponse response;
        try {
            Class<?> responseClass = waitingCommand.getResponseClass();
            response = (AbstractResponse) responseClass.newInstance();
        } catch (Exception ex) {
            pettPlantService.dispatchCommError(CommunicationErrorType.UNKNOWN_RESPONSE,
                  "unable to create response instance", null);
            return;
        }

        if (!response.validateChecksum(responseBytes)) {
            pettPlantService.dispatchCommError(CommunicationErrorType.MALFORMED_RESPONSE,
                  "response checksum did not match", null);
            return;
        }

        // populate the response object with the data in the response bytes
        response.fromResponseBytes(responseBytes);

        // notify all listeners data has been received
        pettPlantService.dispatchCommReceived();

        TimeoutBackgroundRunnable timeout = waitingCommandsToTimeouts.get(waitingCommand);
        backgroundHandler.removeCallbacks(timeout);

        uiHandler.post(new ResponseCallbackUiRunnable(waitingCommand.getResponseCallback(), response));

//		if (waitingCommand.isInterval()) {
//			// is interval, expecting another, so reset the timeout
//			backgroundHandler.postDelayed(timeout, waitingCommand.getResponseTimeoutMs());
//		} else {
        commandIdToWaitingCommands.remove(waitingCommand.getCommandId());
        responseIdToWaitingCommands.remove(waitingCommand.getResponseId());
//		}
    }

    /**
     * Sends an Response on the callback
     */
    private class ResponseCallbackUiRunnable implements Runnable {

        @SuppressWarnings("rawtypes")
        private final ResponseCallback callback;
        private final AbstractResponse response;

        ResponseCallbackUiRunnable(ResponseCallback<?> callback, AbstractResponse response) {
            this.callback = callback;
            this.response = response;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            callback.onResponse(response);
        }
    }

    /**
     * Run on a delay to invoke the timeout after the configured amount of time.
     */
    private class TimeoutBackgroundRunnable implements Runnable {

        private final AbstractCommand<?> command;

        TimeoutBackgroundRunnable(AbstractCommand<?> command) {
            this.command = command;
        }

        @Override
        public void run() {
            // check if the command we were waiting for still hasn't been handled
            Byte responseId = command.getResponseId();
            Byte commandId = command.getCommandId();
            if (responseIdToWaitingCommands.containsKey(responseId)) {

                responseIdToWaitingCommands.remove(responseId);
                commandIdToWaitingCommands.remove(commandId);
                waitingCommandsToTimeouts.remove(command);

                uiHandler.post(new TimeoutCallbackUiRunnable(command.getResponseCallback()));

                ErrorHandler errorHandler = ErrorHandler.getInstance();
                if (errorHandler != null) {
                    errorHandler.logError(Level.INFO, "CommunicationManager$TimeoutBackgroundRunnable" +
                          ".run() - Plant response timed out.", 0, 0);
                } else {
                    if (MyDebug.LOG) {
                        Log.d(TAG, "errorHandler is null.");
                    }
                }
            }
        }
    }

    /**
     * Sends an ErrorType on the callback
     */
    private class TimeoutCallbackUiRunnable implements Runnable {

        @SuppressWarnings("rawtypes")
        private final ResponseCallback callback;

        TimeoutCallbackUiRunnable(ResponseCallback<?> callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            callback.onFailed(CommunicationErrorType.TIMEOUT_RESPONSE);
        }
    }

    /**
     * Listens to the CommAdapter for received bytes
     */
    private class MyCommAdapterListener implements CommAdapterListener {

        @Override
        public void onConnectionState(ConnectionState connectionState) {

            switch (connectionState) {
                case ESTABLISHED:
                    pettPlantService.onCommConnected();
                    break;
                case CONNECTING:
                    pettPlantService.onCommConnecting();
                    break;
                case NONE:
                    pettPlantService.onCommDisconnected();
                    break;
                case FAILED:
                    pettPlantService.onCommFailed();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onReceiveBytes(byte[] response) {
            handleResponseBytes(response);
        }
    }

    /**
     * Listeners that want to be notified of the status of the CommunicationService
     */
    public interface CommunicationManagerListener {

        void onDataReceived();

        void onDataSent();

        void onConnecting();

        void onConnected();

        void onDisconnected();

        void onError(CommunicationErrorType type);
    }
}
