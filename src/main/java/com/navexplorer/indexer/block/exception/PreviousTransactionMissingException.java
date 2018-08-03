package com.navexplorer.indexer.block.exception;

import com.navexplorer.indexer.block.event.MissingTransactionEvent;
import com.navexplorer.indexer.exception.IndexerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class PreviousTransactionMissingException extends IndexerException {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public PreviousTransactionMissingException(String message, String hash) {
        super(message + ": " + hash);

        applicationEventPublisher.publishEvent(new MissingTransactionEvent(this, hash));
    }
}
