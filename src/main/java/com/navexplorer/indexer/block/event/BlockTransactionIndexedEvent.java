package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class BlockTransactionIndexedEvent extends ApplicationEvent {
    @Getter
    private Block block;

    @Getter
    private BlockTransaction transaction;

    public BlockTransactionIndexedEvent(Object source, Block block, BlockTransaction transaction) {
        super(source);
        this.block = block;
        this.transaction = transaction;
    }
}
