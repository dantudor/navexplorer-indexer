package com.navexplorer.indexer.exception;

public class BlockIndexingNotActiveException extends UpdaterException {
    public BlockIndexingNotActiveException() {
        super();
    }

    public BlockIndexingNotActiveException(String message) {
        super(message);
    }
}
