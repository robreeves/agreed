package com.robertpreeves.agreed;

public class NoConsensusException extends Exception {
    public NoConsensusException(String message) {
        super(message);
    }

    public NoConsensusException() {
    }
}
