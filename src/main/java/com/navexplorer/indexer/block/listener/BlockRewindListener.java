package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.address.rewinder.AddressRewinder;
import com.navexplorer.indexer.block.event.BlockRewindEvent;
import com.navexplorer.indexer.block.rewinder.SignalRewinder;
import com.navexplorer.library.block.entity.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BlockRewindListener implements ApplicationListener<BlockRewindEvent> {
    @Autowired
    SignalRewinder signalRewinder;

    @Autowired
    AddressRewinder addressRewinder;

    @Override
    public void onApplicationEvent(BlockRewindEvent event) {
        Block block = event.getBlock();

        signalRewinder.rewindBlock(block);
        addressRewinder.rewind(block);
    }
}
