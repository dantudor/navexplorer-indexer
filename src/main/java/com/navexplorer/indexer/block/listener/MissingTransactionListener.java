package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.block.event.MissingTransactionEvent;
import com.navexplorer.indexer.block.rewinder.BlockRewinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MissingTransactionListener implements ApplicationListener<MissingTransactionEvent> {
    private static final Logger logger = LoggerFactory.getLogger(MissingTransactionEvent.class);

    @Autowired
    BlockRewinder blockRewinder;

    @Override
    public void onApplicationEvent(MissingTransactionEvent event) {
        logger.info("Missing transaction detected, rewinding to hash " + event.getHash());
        blockRewinder.rewindToMissingTransaction(event.getHash());
    }
}
