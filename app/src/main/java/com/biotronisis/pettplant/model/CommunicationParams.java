package com.biotronisis.pettplant.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.persist.AppParams;
import com.biotronisis.pettplant.type.CommunicationType;

import java.util.logging.Level;

public class CommunicationParams {

    // Shared Preferences stuff - http://stackoverflow.com/a/19613702/1130859

    private static final String TAG = "CommunicationParams";

    private static final String COMM_NAME = "name";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final String COMM_ADDRESS = "address";
    private static final String NONE = "none";
    private static final String MOCKING_JAY = "MockingJay";

    private final SharedPreferences appParams;

    private String name;
    private CommunicationType communicationType;
    private String address;

    public CommunicationParams(Context context) {
        appParams = context.getSharedPreferences(AppParams.PETT_PLANT_DATA_FILE, 0);
        name = appParams.getString(COMM_NAME, MOCKING_JAY);
        address = appParams.getString(COMM_ADDRESS, NONE);
        communicationType = CommunicationType.getCommType(appParams.getInt(COMMUNICATION_TYPE, CommunicationType.MOCK.getValue()));
    }

    public boolean saveData() {

        SharedPreferences.Editor editor = appParams.edit();

        editor.putString(COMM_NAME, name);
        editor.putString(COMM_ADDRESS, address);
        editor.putInt(COMMUNICATION_TYPE, communicationType.getValue());

        // Commit the edits!
        boolean success = editor.commit();
        if (!success) {
            if (MyDebug.LOG) {
                Log.e(TAG, "failed to save communicationParams");
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.WARNING, "CommunicationParams.saveData(): " +
                        "Can't update communicationParams - ",
                  R.string.comm_params_persist_error_title,
                  R.string.comm_params_persist_error_message);
        }
        return success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommunicationType getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(CommunicationType communicationType) {
        this.communicationType = communicationType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //	@Override
    public String toString() {
        return "CommunicationParams [name=" + name + ", communicationType=" + communicationType + ", address=" + address + "]";
    }

}
