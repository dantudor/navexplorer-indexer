package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.block.factory.BlockTransactionFactory;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BlockTransactionIndexer {
    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BlockTransactionFactory blockTransactionFactory;

    @Autowired
    private BlockRepository blockRepository;

    public void indexTransaction(String hash) {
        BlockTransaction transaction = blockTransactionFactory.createTransaction(navcoinService.getTransactionByHash(hash));
        blockTransactionService.save(transaction);

        Block block = blockRepository.findByHeight(transaction.getHeight().longValue());

        applicationEventPublisher.publishEvent(new BlockTransactionIndexedEvent(this, block, transaction));
    }
}
