package com.navexplorer.indexer.communityfund.rewinder;

import com.navexplorer.library.communityfund.repository.ProposalVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProposalVoteRewinder {
    @Autowired
    ProposalVoteRepository proposalVoteRepository;

    public void rewindProposalVotes(int height) {
        proposalVoteRepository.delete(proposalVoteRepository.findAllByHeightGreaterThanEqual(height));
    }
}
