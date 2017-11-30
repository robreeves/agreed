package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;

public class PaxosNode<T> implements AgreedNode<T> {
    private final LocalPaxosAcceptor<String> acceptor;
    private final PaxosProposer<String> proposer;
    private final PaxosHttpAcceptor<String> httpAcceptor;
    private final Class<T> valueType;
    private final Gson gson = new Gson();

    public PaxosNode(LocalPaxosAcceptor<String> acceptor, PaxosProposer<String> proposer,
            PaxosHttpAcceptor<String> httpAcceptor, Class<T> valueType) {
        this.acceptor = acceptor;
        this.proposer = proposer;
        this.httpAcceptor = httpAcceptor;
        this.valueType = valueType;
    }

    @Override
    public T propose(T value) throws NoConsensusException {
        String jsonProposal = gson.toJson(value);
        String jsonConsensus = proposer.propose(jsonProposal);
        return gson.fromJson(jsonConsensus, valueType);
    }

    @Override
    public T current() throws NoConsensusException {
        String jsonConsensus = proposer.getCurrent();
        return gson.fromJson(jsonConsensus, valueType);
    }

    @Override
    public void close() throws Exception {
        httpAcceptor.close();
        proposer.close();
        acceptor.close();
    }
}
