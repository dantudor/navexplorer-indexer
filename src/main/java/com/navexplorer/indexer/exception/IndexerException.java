package com.navexplorer.indexer.exception;

public class IndexerException extends RuntimeException {
    public IndexerException() {
        super();
    }

    public IndexerException(String message) {
        super(message);
    }

    public IndexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexerException(Throwable cause) {
        super(cause);
    }
}
