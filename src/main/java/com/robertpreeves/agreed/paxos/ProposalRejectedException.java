package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.NoConsensusException;

public class ProposalRejectedException extends NoConsensusException {
    public ProposalRejectedException() {
    }

    public ProposalRejectedException(String message) {
        super(message);
    }
}
