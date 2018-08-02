package com.navexplorer.indexer.block.exception;

import com.navexplorer.indexer.exception.IndexerException;

public class BlockIndexingNotActiveException extends IndexerException {
    public BlockIndexingNotActiveException() {
        super();
    }

    public BlockIndexingNotActiveException(String message) {
        super(message);
    }
}
