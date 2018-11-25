package com.navexplorer.indexer.communityfund.listener;

import com.navexplorer.indexer.block.event.BlockIndexedEvent;
import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.communityfund.indexer.ProposalIndexer;
import com.navexplorer.indexer.communityfund.indexer.ProposalVoteIndexer;
import com.navexplorer.indexer.communityfund.rewinder.ProposalVoteRewinder;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.service.BlockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProposalVoteIndexListener implements ApplicationListener<BlockIndexedEvent> {
    @Autowired
    private ProposalVoteIndexer proposalVoteIndexer;

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Override
    public void onApplicationEvent(BlockIndexedEvent event) {
        Block block = event.getBlock();

        proposalVoteIndexer.indexProposalVotes(block, blockTransactionService.getByBlockHash(block.getHash()));
    }
}
