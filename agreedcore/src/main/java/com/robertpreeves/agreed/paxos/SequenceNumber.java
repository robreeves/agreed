package com.robertpreeves.agreed.paxos;

/**
 * Represents the sequence number for an instance of Paxos.
 * The sequence number is a long where bits 7-0 represent the node Id.
 * This is used to ensure that the sequence number is unique.
 * Bits 63-8 represent the monotonically increasing round number.
 * This ensures that the sequence number is always greater than previous sequence numbers.
 *
 * The total number of rounds is 2^56.
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
 * Sequence Number: 1-00000011 (259)
 *
 * Node 3 is proposer
 * Sequence Number: 10-00000011 (515)
 *
 * Node 2 is proposer
 * Sequence Number: 11-00000010 (770)
 */
class SequenceNumber {
    private static final int NODEID_OFFSET = Byte.SIZE;
    private static final long MAX_ROUND = Long.MAX_VALUE >> NODEID_OFFSET;

    /**
     * Calculates the next sequence number to use
     *
     * @param nodeId The proposer node Id
     * @param sequenceNumber
     * @return The next sequence number to use during a proposal. This is an unsigned long.
     */
    public static long getNext(byte nodeId, long sequenceNumber) {
        //get current round number
        long nextSequenceNumber = sequenceNumber >> NODEID_OFFSET;

        //increment round number
        ++nextSequenceNumber;
        if (Long.compareUnsigned(nextSequenceNumber, MAX_ROUND) > 0) {
            throw new IllegalStateException("The sequence number has exceeded the maximum value " +
                    "that it can represent");
        }

        //append nodeId bits
        nextSequenceNumber = (nextSequenceNumber << NODEID_OFFSET) | nodeId;

        return nextSequenceNumber;
    }
}
