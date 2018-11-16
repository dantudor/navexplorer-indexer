package com.navexplorer.indexer.communityfund.factory;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.communityfund.entity.ProposalVote;
import com.navexplorer.library.block.entity.OutputType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalVoteFactory {
    public List<ProposalVote> createProposalVotes(Block block, BlockTransaction transaction) {
        List<ProposalVote> proposalVotes = new ArrayList<>();

        transaction.getOutputs().forEach(output -> {
            if (output.getType().equals(OutputType.PROPOSAL_YES_VOTE)) {
                ProposalVote vote = new ProposalVote();
                vote.setAddress(block.getStakedBy());
                vote.setHeight(block.getHeight().intValue());
                vote.setProposal(output.getHash());
                vote.setVote(true);
                proposalVotes.add(vote);
            } else if (output.getType().equals(OutputType.PROPOSAL_NO_VOTE)) {
                ProposalVote vote = new ProposalVote();
                vote.setAddress(block.getStakedBy());
                vote.setHeight(block.getHeight().intValue());
                vote.setProposal(output.getHash());
                vote.setVote(false);
                proposalVotes.add(vote);
            }
        });

        return proposalVotes;
    }
}
