package com.navexplorer.indexer.block.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MissingTransactionEvent extends ApplicationEvent {
    @Getter
    private String hash;

    public MissingTransactionEvent(Object source, String hash) {
        super(source);
        this.hash = hash;
    }
}
