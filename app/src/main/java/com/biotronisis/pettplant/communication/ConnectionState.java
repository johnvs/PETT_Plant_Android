package com.biotronisis.pettplant.communication;

public enum ConnectionState {
	
	NONE(0),          // we're doing nothing
	FAILED(1),        // an attempted connection failed
	LISTENING(2),     // now listening for incoming connections
	CONNECTING(3),    // now initiating an outgoing connection
	ESTABLISHED(4);   // now connected to a remote device

	private int id;

	ConnectionState(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}

	public int getId() {
		return id;
	}

}
