package com.navexplorer.indexer.block.event;

import org.springframework.context.ApplicationEvent;

public class OrphanedBlockEvent extends ApplicationEvent {
    public OrphanedBlockEvent(Object source) {
        super(source);
    }
}
