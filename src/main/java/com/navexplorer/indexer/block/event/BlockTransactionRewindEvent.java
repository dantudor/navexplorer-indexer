package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.BlockTransaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class BlockTransactionRewindEvent extends ApplicationEvent {
    @Getter
    private BlockTransaction transaction;

    public BlockTransactionRewindEvent(Object source, BlockTransaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
