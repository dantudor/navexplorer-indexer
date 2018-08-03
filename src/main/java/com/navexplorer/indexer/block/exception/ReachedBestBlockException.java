package com.navexplorer.indexer.block.exception;

import com.navexplorer.indexer.exception.IndexerException;

public class ReachedBestBlockException extends IndexerException {
    public ReachedBestBlockException() {
        super();
    }

    public ReachedBestBlockException(String message) {
        super(message);
    }
}
