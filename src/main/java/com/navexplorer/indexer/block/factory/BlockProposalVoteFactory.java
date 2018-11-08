package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionProposalVote;
import com.navexplorer.library.block.entity.OutputType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockProposalVoteFactory {
    public List<BlockTransactionProposalVote> createProposalVotes(BlockTransaction transaction) {
        List<BlockTransactionProposalVote> proposalVotes = new ArrayList<>();

        transaction.getOutputs().forEach(output -> {
            if (output.getType().equals(OutputType.PROPOSAL_YES_VOTE)) {
                BlockTransactionProposalVote vote = new BlockTransactionProposalVote();
                vote.setHash(output.getHash());
                vote.setVote(true);
                proposalVotes.add(vote);
            } else if (output.getType().equals(OutputType.PROPOSAL_NO_VOTE)) {
                BlockTransactionProposalVote vote = new BlockTransactionProposalVote();
                vote.setHash(output.getHash());
                vote.setVote(false);
                proposalVotes.add(vote);
            }
        });

        return proposalVotes;
    }
}
