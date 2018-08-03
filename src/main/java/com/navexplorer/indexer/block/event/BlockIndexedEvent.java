package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.Block;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class BlockIndexedEvent extends ApplicationEvent {
    @Getter
    private Block block;

    public BlockIndexedEvent(Object source, Block block) {
        super(source);
        this.block = block;
    }
}
