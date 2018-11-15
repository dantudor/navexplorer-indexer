package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.block.factory.BlockTransactionFactory;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BlockTransactionIndexer {
    @Autowired
    NavcoinService navcoinService;

    @Autowired
    BlockTransactionService blockTransactionService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    BlockTransactionFactory blockTransactionFactory;

    public void indexTransaction(String hash) {
        BlockTransaction transaction = blockTransactionFactory.createTransaction(navcoinService.getTransactionByHash(hash));
        blockTransactionService.save(transaction);

        applicationEventPublisher.publishEvent(new BlockTransactionIndexedEvent(this, transaction));
    }
}
