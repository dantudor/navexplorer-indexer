package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.block.event.OrphanedBlockEvent;
import com.navexplorer.indexer.block.rewinder.BlockRewinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OrphanedBlockListener implements ApplicationListener<OrphanedBlockEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OrphanedBlockListener.class);

    @Autowired
    BlockRewinder blockRewinder;

    @Override
    public void onApplicationEvent(OrphanedBlockEvent event) {
        logger.info("Orphan block detected, rewinding 10 blocks.");
        blockRewinder.rewindTop10Blocks();
    }
}
