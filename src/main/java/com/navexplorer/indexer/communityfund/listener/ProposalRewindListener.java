package com.navexplorer.indexer.communityfund.listener;

import com.navexplorer.indexer.block.event.BlockTransactionRewindEvent;
import com.navexplorer.indexer.communityfund.indexer.ProposalIndexer;
import com.navexplorer.indexer.communityfund.rewinder.ProposalRewinder;
import com.navexplorer.indexer.communityfund.rewinder.ProposalVoteRewinder;
import com.navexplorer.library.block.entity.BlockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalRewindListener implements ApplicationListener<BlockTransactionRewindEvent> {
    @Autowired
    ProposalRewinder proposalRewinder;

    @Autowired
    ProposalVoteRewinder proposalVoteRewinder;

    @Override
    public void onApplicationEvent(BlockTransactionRewindEvent event) {
        BlockTransaction transaction = event.getTransaction();

        if (transaction.isSpend() && !transaction.getVersion().equals(4)) {
            proposalRewinder.rewindProposal(transaction);
        }

        if (transaction.isEmpty()) {
            proposalVoteRewinder.rewindProposalVotes(transaction.getHeight());
        }
    }
}
