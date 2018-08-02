package com.navexplorer.indexer.address.transition;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import com.navexplorer.indexer.address.event.AddressRewindEvent;
import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.service.AddressService;
import com.navexplorer.library.address.service.AddressTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AddressTransition {
    @Autowired
    AddressService addressService;

    @Autowired
    AddressTransactionService addressTransactionService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void up(AddressTransaction transaction) {
        Address address = addressService.getAddress(transaction.getAddress());

        switch (transaction.getType()) {
            case COMMUNITY_FUND:
                address.setReceivedCount(address.getReceivedCount() + 1);
                address.setReceived(address.getReceived() + transaction.getReceived());
                address.setBalance(address.getBalance() + transaction.getReceived());
                break;

            case SEND:
                Double amountSent = transaction.getSent() - transaction.getReceived();

                address.setSentCount(address.getSentCount() + 1);
                address.setSent(address.getSent() + amountSent);
                address.setBalance(address.getBalance() - amountSent);
                break;

            case RECEIVE:
                Double amountReceived = transaction.getReceived() - transaction.getSent();

                address.setReceivedCount(address.getReceivedCount() + 1);
                address.setReceived(address.getReceived() + amountReceived);
                address.setBalance(address.getBalance() + amountReceived);
                break;

            case STAKING:
                Double amountStaked = transaction.getReceived() - transaction.getSent();

                if (transaction.getColdStaking()) {
                    address.setColdStakedCount(address.getColdStakedCount() + 1);
                    address.setColdStakedSent(address.getColdStakedSent() + transaction.getSent());
                    address.setColdStakedReceived(address.getStakedReceived() + transaction.getReceived());
                    address.setColdStaked(address.getColdStaked() + amountStaked);
                    address.setBalance(address.getBalance() + amountStaked);

                } else {
                    address.setStakedCount(address.getStakedCount() + 1);
                    address.setStakedSent(address.getStakedSent() + transaction.getSent());
                    address.setStakedReceived(address.getStakedReceived() + transaction.getReceived());
                    address.setStaked(address.getStaked() + amountStaked);
                    address.setBalance(address.getBalance() + amountStaked);
                }
                break;
        }

        if (transaction.getColdStaking() == null) {
            transaction.setColdStaking(false);
        }

        transaction.setBalance(address.getBalance());
        address.setBlockIndex(transaction.getHeight());

        addressTransactionService.save(transaction);
        addressService.save(address);

        applicationEventPublisher.publishEvent(new AddressIndexedEvent(this, transaction));
    }

    public void down(AddressTransaction transaction) {
        Address address = addressService.getAddress(transaction.getAddress());

        switch (transaction.getType()) {
            case COMMUNITY_FUND:
                address.setReceivedCount(address.getReceivedCount() - 1);
                address.setReceived(address.getReceived() - transaction.getReceived());
                address.setBalance(address.getBalance() - transaction.getReceived());
                break;

            case SEND:
                Double amountSent = transaction.getSent() - transaction.getReceived();

                address.setSentCount(address.getSentCount() - 1);
                address.setSent(address.getSent() - amountSent);
                address.setBalance(address.getBalance() + amountSent);
                break;

            case RECEIVE:
                Double amountReceived = transaction.getReceived() - transaction.getSent();

                address.setReceivedCount(address.getReceivedCount() - 1);
                address.setReceived(address.getReceived() - amountReceived);
                address.setBalance(address.getBalance() - amountReceived);
                break;

            case STAKING:
                Double amountStaked = transaction.getReceived() - transaction.getSent();

                if (transaction.getColdStaking()) {
                    address.setColdStakedCount(address.getColdStakedCount() - 1);
                    address.setColdStakedSent(address.getColdStakedSent() - transaction.getSent());
                    address.setColdStakedReceived(address.getStakedReceived() - transaction.getReceived());
                    address.setColdStaked(address.getColdStaked() - amountStaked);
                    address.setBalance(address.getBalance() - amountStaked);

                } else {
                    address.setStakedCount(address.getStakedCount() - 1);
                    address.setStakedSent(address.getStakedSent() - transaction.getSent());
                    address.setStakedReceived(address.getStakedReceived() - transaction.getReceived());
                    address.setStaked(address.getStaked() - amountStaked);
                    address.setBalance(address.getBalance() - amountStaked);
                }
                break;
        }

        AddressTransaction lastTransaction = addressTransactionService.getLastTransactionsForAddress(address.getHash());
        address.setBlockIndex(lastTransaction == null ? 0 : lastTransaction.getHeight());

        addressService.save(address);
        addressTransactionService.delete(transaction);

        applicationEventPublisher.publishEvent(new AddressRewindEvent(this, transaction));
    }
}
