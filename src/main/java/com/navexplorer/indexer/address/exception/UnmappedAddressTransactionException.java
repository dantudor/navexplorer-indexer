package com.navexplorer.indexer.address.exception;

import com.navexplorer.indexer.exception.IndexerException;

public class UnmappedAddressTransactionException extends IndexerException {
    public UnmappedAddressTransactionException(String message) {
        super(message);
    }
}
