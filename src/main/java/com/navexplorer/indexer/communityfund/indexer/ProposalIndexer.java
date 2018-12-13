package com.navexplorer.indexer.communityfund.indexer;

import org.springframework.dao.DuplicateKeyException;
import com.navexplorer.indexer.communityfund.factory.ProposalFactory;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.communityfund.entity.Proposal;
import com.navexplorer.library.communityfund.entity.ProposalState;
import com.navexplorer.library.communityfund.repository.ProposalRepository;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.navcoin.exception.NavcoinException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProposalIndexer {
    private static final Logger logger = LoggerFactory.getLogger(ProposalIndexer.class);

    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private ProposalFactory proposalFactory;

    @Autowired
    private ProposalRepository proposalRepository;

    public void indexProposal(BlockTransaction transaction) {
        if (!transaction.isSpend() || !transaction.getVersion().equals(4)) {
            return;
        }

        Proposal proposal = null;

        try {
            proposal = proposalFactory.createProposal(
                    navcoinService.getProposal(transaction.getHash()), transaction.getTime(), transaction.getHeight());

            proposalRepository.save(proposal);

            logger.info("Community fund proposal saved: " + proposal.getHash());
        } catch (NavcoinException e) {
            logger.error("Community fund proposal not found in tx : " + transaction.getHash());

        } catch (DuplicateKeyException e) {
            if (proposal != null) {
                updateProposal(proposalRepository.findOneByHash(proposal.getHash()));
            }
        }
    }

    public void updateAllProposals() {
        updateProposalsByState(ProposalState.PENDING);
        updateProposalsByState(ProposalState.PENDING_FUNDS);
        updateProposalsByState(ProposalState.ACCEPTED);
        updateProposalsByState(ProposalState.REJECTED);
        updateProposalsByState(ProposalState.EXPIRED);
    }

    private void updateProposalsByState(ProposalState state) {
        proposalRepository.findAllByStateOrderByIdDesc(state).forEach(this::updateProposal);
    }

    private void updateProposal(Proposal proposal) {
        proposalFactory.updateProposal(proposal, navcoinService.getProposal(proposal.getHash()));
        proposalRepository.save(proposal);

        logger.info(String.format("Proposal updated: %s %s", proposal.getState(), proposal.getHash()));
    }
}
