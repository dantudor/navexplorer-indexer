package com.navexplorer.indexer.communityfund.rewinder;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.communityfund.entity.Proposal;
import com.navexplorer.library.communityfund.repository.ProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProposalRewinder {
    private static final Logger logger = LoggerFactory.getLogger(ProposalRewinder.class);

    @Autowired
    private ProposalRepository proposalRepository;

    public void rewindProposal(BlockTransaction transaction) {
        if (!transaction.isSpend() || !transaction.getVersion().equals(4)) {
            return;
        }

        Proposal proposal = proposalRepository.findOneByHash(transaction.getHash());

        if (proposal != null) {
            proposalRepository.delete(proposal);

            logger.info("Community fund - Proposal deleted: " + proposal.getHash());
        }
    }
}
