package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;

public interface PaxosAcceptorState<T> {
    /**
     * Gets the most recently promised sequence number as an unsigned long
     * @return
     */
    long getPromiseSeqNumber();

    /**
     * Sets the most recently promised sequence number
     * @param seqNumber The new sequence number as an unsigned long
     */
    void setPromiseSeqNumber(long seqNumber);

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
     * @param committed
     */
    void setCommitted(Accept<T> committed);
}
