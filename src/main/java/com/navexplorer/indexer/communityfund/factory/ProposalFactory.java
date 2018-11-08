package com.navexplorer.indexer.communityfund.factory;

import com.navexplorer.library.communityfund.entity.Proposal;
import com.navexplorer.library.communityfund.entity.ProposalState;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProposalFactory {
    public Proposal createProposal(org.navcoin.response.Proposal apiProposal, Date createdAt, int height) {
        Proposal proposal = new Proposal();
        proposal.setCreatedAt(createdAt);
        proposal.setHeight(height);

        return updateProposal(proposal, apiProposal);
    }

    public Proposal updateProposal(Proposal proposal, org.navcoin.response.Proposal apiProposal) {
        proposal.setVersion(apiProposal.getVersion());
        proposal.setHash(apiProposal.getHash());
        proposal.setBlockHash(apiProposal.getBlockHash());
        proposal.setDescription(apiProposal.getDescription());
        proposal.setRequestedAmount(apiProposal.getRequestedAmount());
        proposal.setNotPaidYet(apiProposal.getNotPaidYet());
        proposal.setUserPaidFee(apiProposal.getUserPaidFee());
        proposal.setPaymentAddress(apiProposal.getPaymentAddress());
        proposal.setProposalDuration(apiProposal.getProposalDuration());
        proposal.setState(ProposalState.fromId(apiProposal.getState()));
        proposal.setStatus(apiProposal.getStatus());
        proposal.setStateChangedOnBlock(apiProposal.getStateChangedOnBlock());
        proposal.setVotesYes(apiProposal.getVotesYes());
        proposal.setVotesNo(apiProposal.getVotesNo());
        proposal.setVotingCycle(apiProposal.getVotingCycle());

        if (apiProposal.getExpiresOn() != null) {
            Date expiresOn = new Date();
            expiresOn.setTime(apiProposal.getExpiresOn()*1000);
            proposal.setExpiresOn(expiresOn);
        }

        return proposal;
    }
}
