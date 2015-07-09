package com.biotronisis.pettplant.communication;

public enum ConnectionState {
	
	NONE,          // we're doing nothing
	FAILED,        // an attempted connection failed
	LISTENING,     // now listening for incoming connections
	CONNECTING,    // now initiating an outgoing connection
	ESTABLISHED;   // now connected to a remote device
}
