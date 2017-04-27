package com.robertpreeves.agreed.paxos;

import org.junit.Assert;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class SequenceNumberTest {
    @Test
    public void nextSequenceNumberTest() {
        //third round, node 3, 3 nodes total
        long sequenceStart = 515;
        SequenceNumber sequenceNumber = new SequenceNumber(sequenceStart);
        Assert.assertEquals(sequenceStart, sequenceNumber.getSequenceNumber());

        byte nodeId = 2;
        long nextSequence = sequenceNumber.getNext(nodeId);

        Assert.assertEquals(770, nextSequence);
    }

    @Test public void outOfRangeTest() {
        byte nodeId = 1;
        long startingRound = (Long.MAX_VALUE >> Byte.SIZE) - 1;
        long startingSequenceNumber = startingRound << Byte.SIZE | nodeId;
        SequenceNumber sequenceNumber = new SequenceNumber(startingSequenceNumber);

        //in range
        long nextSeqNumber = sequenceNumber.getNext(nodeId);

        //out of range
        SequenceNumber seqNumberLastInRange = new SequenceNumber(nextSeqNumber);
        assertThatThrownBy(() -> seqNumberLastInRange.getNext(nodeId))
                .isInstanceOf(IllegalStateException.class);
    }
}
