package com.navexplorer.indexer.exception;

public class PreviousTransactionMissingException extends UpdaterException {
    private String hash;

    public PreviousTransactionMissingException() {
        super();
    }

    public PreviousTransactionMissingException(String message) {
        super(message);
    }

    public PreviousTransactionMissingException(String message, String hash) {
        super(message);
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
