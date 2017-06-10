package com.robertpreeves.agreed;

/**
 * This exception represents the case where the nodes cannot come to consensus
 */
public class NoConsensusException extends Exception {
    public NoConsensusException(String message) {
        super(message);
    }

    public NoConsensusException() {
    }
}
