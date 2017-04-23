package com.robertpreeves.agreed.paxos;

import org.junit.Assert;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class SequenceNumberTest {
    @Test
    public void nextSequenceNumberTest() {
        //third round, node 3, 3 nodes total
        long sequenceStart = 1539;
        SequenceNumber sequenceNumber = new SequenceNumber(sequenceStart);
        Assert.assertEquals(sequenceStart, sequenceNumber.getSequenceNumber());

        //before next, round number is 6
        //after next, it should be 6 + nodeCount
        byte nodeId = 2;
        int nodeCount = 3;
        long nextSequence = sequenceNumber.getNext(nodeId, nodeCount);

        Assert.assertEquals(2306, nextSequence);
    }

    @Test public void outOfRangeTest() {
        byte nodeId = 1;
        int nodeCount = 3;
        long startingRound = (Long.MAX_VALUE >> Byte.SIZE) - 5;
        long startingSequenceNumber = startingRound << Byte.SIZE | nodeId;
        SequenceNumber sequenceNumber = new SequenceNumber(startingSequenceNumber);

        //in range
        long nextSeqNumber = sequenceNumber.getNext(nodeId, nodeCount);

        //out of range
        SequenceNumber seqNumberLastInRange = new SequenceNumber(nextSeqNumber);
        assertThatThrownBy(() -> seqNumberLastInRange.getNext(nodeId, nodeCount))
                .isInstanceOf(IllegalStateException.class);
    }
}
