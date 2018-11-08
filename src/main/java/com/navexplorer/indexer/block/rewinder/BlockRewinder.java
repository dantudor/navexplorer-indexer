package com.navexplorer.indexer.block.rewinder;

import com.navexplorer.indexer.block.event.BlockRewindEvent;
import com.navexplorer.indexer.block.event.BlockTransactionRewindEvent;
import com.navexplorer.indexer.block.service.BlockIndexingActiveService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.navcoin.response.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BlockRewinder {
    private static final Logger logger = LoggerFactory.getLogger(BlockRewinder.class);

    @Autowired
    BlockIndexingActiveService blockIndexingActiveService;

    @Autowired
    BlockTransactionService blockTransactionService;

    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    NavcoinService navcoinService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void rewindTop10Blocks() {
        if (!blockIndexingActiveService.isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info("Rewinding top 10 blocks");
        blockRepository.findTop10ByOrderByHeightDesc().forEach(this::rewindBlock);
    }

    public void rewindToMissingTransaction(String hash) {
        if (!blockIndexingActiveService.isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        Transaction transaction = navcoinService.getTransactionByHash(hash);

        if (transaction != null) {
            rewindToHeight(transaction.getHeight().longValue());
        }
    }

    public void rewindToHeight(Long height) {
        if (!blockIndexingActiveService.isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info("Rewinding to height " + height);
        Block block = blockRepository.findFirstByOrderByHeightDesc();
        if (block == null) {
            logger.error("No blocks found");
        } else if (block.getHeight() > height) {
            rewindBlock(block);
            rewindToHeight(height);
        }
    }

    private void rewindBlock(Block block) {
        if (!blockIndexingActiveService.isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info(String.format("Rewinding block %s", block.getHeight()));

        blockTransactionService.getByHeight(block.getHeight()).forEach(blockTransaction -> {
            applicationEventPublisher.publishEvent(new BlockTransactionRewindEvent(this, blockTransaction));
            blockTransactionRepository.delete(blockTransaction);
        });

        applicationEventPublisher.publishEvent(new BlockRewindEvent(this, block));
        blockRepository.delete(block);
    }
}
