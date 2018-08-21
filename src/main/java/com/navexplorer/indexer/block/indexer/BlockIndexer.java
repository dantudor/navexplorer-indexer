package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.event.BlockIndexedEvent;
import com.navexplorer.indexer.block.event.OrphanedBlockEvent;
import com.navexplorer.indexer.block.exception.*;
import com.navexplorer.indexer.block.factory.BlockFactory;
import com.navexplorer.indexer.block.service.BlockIndexingActiveService;
import com.navexplorer.indexer.exception.IndexerException;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BlockIndexer {
    @Autowired
    private BlockIndexingActiveService blockIndexingActiveService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private BlockTransactionRepository blockTransactionRepository;

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Autowired
    private BlockTransactionIndexer blockTransactionIndexer;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BlockFactory blockFactory;

    public void indexAllBlocks() {
        Boolean indexing = true;
        while (indexing) {
            try {
                indexBlocks();
            } catch (IndexerException e) {
                indexing = false;
            }
        }
    }

    public Block indexBlocks() throws IndexerException {
        try {
            mustBeActive();

            Block bestBlock = blockService.getBestBlock();
            Long bestHeight = bestBlock == null ? 0L : bestBlock.getHeight();

            org.navcoin.response.Block apiBlock = navcoinService.getBlockByHeight(bestHeight + 1);
            if (apiBlock == null) {
                throw new ReachedBestBlockException("Best block is " + bestHeight);
            }

            if (blockIsOrphan(bestBlock, apiBlock)) {
                throw new OrphanBlockException(String.format("Building on a orphan block at height: %s", bestBlock.getHeight()));
            }

            return indexBlock(apiBlock);
        } catch (OrphanBlockException e) {
            applicationEventPublisher.publishEvent(new OrphanedBlockEvent(this));
        }

        return null;
    }

    private Block indexBlock(org.navcoin.response.Block apiBlock) {
        Block block = blockFactory.createBlock(apiBlock);
        blockService.save(block);

        apiBlock.getTx().forEach(blockTransactionIndexer::indexTransaction);

        updateFeesAndSpendForBlock(block);
        updateStakingInfo(block);

        blockService.save(block);

        applicationEventPublisher.publishEvent(new BlockIndexedEvent(this, block));

        return block;
    }

    private void updateFeesAndSpendForBlock(Block block) {
        blockTransactionService.getByHeight(block.getHeight()).forEach(transaction -> {
            block.setFees(block.getFees() + transaction.getFees());
            if (transaction.isSpend()) {
                block.setSpend(block.getSpend() + transaction.getOutputAmount());
            }
        });
    }

    private void updateStakingInfo(Block block) {
        BlockTransaction transaction = blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(block.getHash(), 0.0);
        if (transaction == null) {
            return;
        }

        block.setStake(transaction.getStake());

        transaction.getOutputs().stream().filter(o -> o.getAddresses().size() > 0).findFirst()
                .ifPresent(output -> block.setStakedBy(output.getAddresses().get(0)));
    }

    private Boolean blockIsOrphan(Block bestBlock, org.navcoin.response.Block apiBlock) {
        return bestBlock != null && !apiBlock.getPreviousblockhash().equals(bestBlock.getHash());
    }

    private void mustBeActive() {
        if (!blockIndexingActiveService.isActive()) {
            throw new BlockIndexingNotActiveException("Block indexing is disabled");
        }
    }
}
