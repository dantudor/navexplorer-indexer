package com.navexplorer.indexer.communityfund.listener;

import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.communityfund.indexer.ProposalIndexer;
import com.navexplorer.library.block.entity.BlockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalIndexListener implements ApplicationListener<BlockTransactionIndexedEvent> {
    @Autowired
    private ProposalIndexer proposalIndexer;

    @Override
    public void onApplicationEvent(BlockTransactionIndexedEvent event) {
        BlockTransaction transaction = event.getTransaction();
        if (!transaction.isSpend() || !transaction.getVersion().equals(4)) {
            return;
        }

        proposalIndexer.indexProposal(transaction);
    }
}
