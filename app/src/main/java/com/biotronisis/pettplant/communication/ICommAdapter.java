package com.biotronisis.pettplant.communication;


public interface ICommAdapter {

    void activate(String address);

    void deactivate();

    void connLost();

    boolean isReConnectingToDevice(String address);

    ConnectionState getConnectionState();

    void sendBytes(byte[] command);

    interface CommAdapterListener {

        void onConnectionState(ConnectionState connectionState);

        void onReceiveBytes(byte[] response);
    }
}
