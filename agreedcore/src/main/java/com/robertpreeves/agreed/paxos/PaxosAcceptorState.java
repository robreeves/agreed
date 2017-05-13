package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;


public interface PaxosAcceptorState<T> extends AutoCloseable {
    /**
     * Gets the most recently promised sequence number as an unsigned long
     * @return
     */
    long getPromised();

    /**
     * Sets the most recently promised sequence number
     * @param seqNumber The new sequence number as an unsigned long
     */
    void setPromised(long seqNumber);

    /**
     * Gets the most recently accepted sequence number
     * @return
     */
    Accept<T> getAccepted();

    /**
     * Sets the current accepted value
     * @param accepted
     */
    void setAccepted(Accept<T> accepted);

    /**
     * Gets the current committed value
     * @return
     */
    Accept<T> getCommitted();

    /**
     * Sets the current committed value
     */
    void setCommitted(Accept<T> committed);
}
