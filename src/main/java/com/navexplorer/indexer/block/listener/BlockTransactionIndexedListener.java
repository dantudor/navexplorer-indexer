package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.block.service.PreviousInputService;
import com.navexplorer.indexer.communityfund.indexer.ProposalIndexer;
import com.navexplorer.library.block.entity.BlockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BlockTransactionIndexedListener implements ApplicationListener<BlockTransactionIndexedEvent> {
    @Autowired
    PreviousInputService previousInputService;

    @Autowired
    ProposalIndexer proposalIndexer;

    @Override
    public void onApplicationEvent(BlockTransactionIndexedEvent event) {
        BlockTransaction transaction = event.getTransaction();

        previousInputService.updateTransaction(transaction);
    }
}
