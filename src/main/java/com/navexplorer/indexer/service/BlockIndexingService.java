package com.navexplorer.indexer.service;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.indexer.exception.OrphanBlockException;
import com.navexplorer.library.block.factory.BlockFactory;
import com.navexplorer.library.block.factory.TransactionFactory;
import com.navexplorer.library.block.service.BlockService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.configuration.service.ConfigurationService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import com.navexplorer.indexer.exception.BlockIndexingNotActiveException;
import com.navexplorer.indexer.exception.PreviousTransactionMissingException;
import com.navexplorer.indexer.exception.UpdaterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockIndexingService {
    private static final Logger logger = LoggerFactory.getLogger(BlockIndexingService.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private BlockStakingService blockStakingService;

    @Autowired
    private SignallingService signallingService;

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Autowired
    private PreviousInputService previousInputService;

    @Autowired
    private RewindService rewindService;

    @Autowired
    private AddressIndexingService addressIndexingService;

    public void indexBlocks() throws UpdaterException {
        while (true) {
            try {
                Block block = saveNewBlock();
                if (block == null) {
                    break;
                }

                Boolean addressIndexed = addressIndexingService.indexBlock(block);
                if (!addressIndexed) {
                    throw new UpdaterException(String.format("Could not index addresses for block: %s", block.getHeight()));
                }
            } catch (OrphanBlockException e) {
                logger.error("Orphan block detected", e);
                rewindService.rewindTop10Blocks();
            } catch (PreviousTransactionMissingException e) {
                logger.error("Previous transaction missing", e);
                rewindService.rewindToMissingTransaction(e.getHash());
                throw e;
            }
        }
    }

    private Block saveNewBlock() throws UpdaterException {
        mustBeActive();

        Block bestBlock = blockService.getBestBlock();
        Long bestHeight = bestBlock == null ? 0L : bestBlock.getHeight();

        org.navcoin.response.Block rawBlock = navcoinService.getBlockByHeight(bestHeight + 1);

        if (rawBlock == null) {
            logger.info(String.format("No new blocks found. Best block height remains at %s", bestHeight));
            return null;
        }

        if (bestBlock != null && !rawBlock.getPreviousblockhash().equals(bestBlock.getHash())) {
            String message = String.format("Building on a orphan block at height: %s", bestBlock.getHeight());
            logger.error(message);
            throw new OrphanBlockException(message);
        }

        logger.info(String.format("Indexing Block - %s", rawBlock.getHeight()));

        Block newBlock = BlockFactory.createBlock(rawBlock);

        signallingService.setSignallingForBlock(newBlock);
        signallingService.updateSoftForks(newBlock);

        blockService.save(newBlock);

        rawBlock.getTx().forEach(this::saveBlockTransaction);
        updateFeesAndSpendForBlock(newBlock);

        blockStakingService.updateStakingInfo(newBlock);
        blockService.save(newBlock);

        return newBlock;
    }

    private void saveBlockTransaction(String hash) {
        BlockTransaction transaction = TransactionFactory.createTransaction(navcoinService.getTransactionByHash(hash));

        if (transaction.getHeight().equals(-1)) {
            System.out.print(navcoinService.getTransactionByHash(hash));
            System.exit(1);
        }

        previousInputService.updateTransaction(transaction);
        blockTransactionService.save(transaction);
    }

    private void updateFeesAndSpendForBlock(Block block) {
        blockTransactionService.getByHeight(block.getHeight()).forEach(transaction -> {
            if (transaction.isSpend()) {
                block.setFees(block.getFees() + transaction.getFees());
                block.setSpend(block.getSpend() + transaction.getOutputAmount());
            }
        });
    }

    private void mustBeActive() {
        if (!(Boolean) configurationService.getBlockIndexActive().getValue()) {
            throw new BlockIndexingNotActiveException("Block indexing is disabled");
        }
    }
}
