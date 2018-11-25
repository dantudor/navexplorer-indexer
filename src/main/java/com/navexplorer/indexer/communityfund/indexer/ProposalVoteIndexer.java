package com.navexplorer.indexer.communityfund.indexer;

import com.navexplorer.indexer.communityfund.factory.ProposalVoteFactory;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.communityfund.entity.ProposalVote;
import com.navexplorer.library.communityfund.repository.ProposalVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProposalVoteIndexer {
    @Autowired
    private ProposalVoteFactory proposalVoteFactory;

    @Autowired
    private ProposalVoteRepository proposalVoteRepository;

    public void indexProposalVotes(Block block, List<BlockTransaction> transactions) {
        List<ProposalVote> proposalVotes = proposalVoteFactory.createProposalVotes(block, transactions);
        if (proposalVotes.size() != 0) {
            proposalVoteRepository.save(proposalVotes);
        }
    }
}
