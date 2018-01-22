package com.biotronisis.pettplant.communication;

/**
 * The errors that may happen during sending and receiving
 */
public enum CommunicationErrorType {

    TRANSMIT_FAILED,
    UNKNOWN_RESPONSE,
    MALFORMED_RESPONSE,
    UNEXPECTED_RESPONSE,
    TIMEOUT_RESPONSE
}
