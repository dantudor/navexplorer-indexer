package com.navexplorer.indexer.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.address.entity.AddressTransaction;

public class AddressTransactionFactory {
    public static AddressTransaction create(String address, BlockTransaction transaction) {
        AddressTransaction addressTransaction = new AddressTransaction();
        addressTransaction.setAddress(address);
        addressTransaction.setTransaction(transaction.getHash());
        addressTransaction.setHeight(transaction.getHeight());
        addressTransaction.setTime(transaction.getTime());

        return addressTransaction;
    }
}
