package com.robertpreeves.agreed.paxos;

/**
 * Represents the sequence number for an instance of Paxos.
 * The sequence number is a long where bits 7-0 represent the node Id.
 * This is used to ensure that the sequence number is unique.
 * Bits 63-8 represent the monotonically increasing round number.
 * The round number increases by the total node count.
 * This ensures that the sequence number is always greater than previous sequence numbers.
 *
 * The total number of rounds is (2^56)/nodeCount.
 *
 * Example:
 * 3 nodes in the group (ids 1,2,3).
 *
 * Sequence number format: bits(63,8)-bits(7,0) (decimal value)
 *
 * Node 1 is proposer
 * Sequence Number: 0-00000001 (1)
 *
 * Node 3 is proposer
 * Sequence Number: 11-00000011 (771)
 *
 * Node 3 is proposer
 * Sequence Number: 110-00000011 (1539)
 *
 * Node 2 is proposer
 * Sequence Number: 1001-00000010 (2306)
 *
 */
class SequenceNumber {
    private static final int NODEID_OFFSET = Byte.SIZE;
    private static final long MAX_ROUND = Long.MAX_VALUE >> NODEID_OFFSET;
    private final long sequenceNumber;

    public SequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the current sequence number
     * @return The sequence number as an unsigned long
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Calculates the next sequence number to use
     * @param nodeId The proposer node Id
     * @param nodeCount The total number of nodes in the group
     * @return The next sequence number to use during a proposal. This is an unsigned long.
     */
    public long getNext(byte nodeId, int nodeCount) {
        //get current round number
        long nextSequenceNumber = sequenceNumber >> NODEID_OFFSET;

        //increment round number
        nextSequenceNumber += nodeCount;
        if (Long.compareUnsigned(nextSequenceNumber, MAX_ROUND) > 0) {
            throw new IllegalStateException("The sequence number has exceeded the maximum value that it can represent");
        }

        //append nodeId bits
        nextSequenceNumber = (nextSequenceNumber << NODEID_OFFSET) | nodeId;

        return nextSequenceNumber;
    }
}
