package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.BlockTransaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class BlockTransactionIndexedEvent extends ApplicationEvent {
    @Getter
    private BlockTransaction transaction;

    public BlockTransactionIndexedEvent(Object source, BlockTransaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
