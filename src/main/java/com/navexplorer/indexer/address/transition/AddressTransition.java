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
        address.setBlockIndex(transaction.getHeight());

        switch (transaction.getType()) {
            case SEND:
                address.send(transaction.getSent() - transaction.getReceived());
                break;

            case RECEIVE:
            case COMMUNITY_FUND:
            case COMMUNITY_FUND_PAYOUT:
                address.receive(transaction.getReceived() - transaction.getSent());
                break;

            case STAKING:
                address.stake(transaction.getSent(), transaction.getReceived());
                break;
        }

        transaction.setBalance(address.getBalance());

        addressTransactionService.save(transaction);
        addressService.save(address);

        applicationEventPublisher.publishEvent(new AddressIndexedEvent(this, transaction));
    }

    public void down(AddressTransaction transaction) {
        Address address = addressService.getAddress(transaction.getAddress());

        switch (transaction.getType()) {
            case SEND:
                Double amountSent = transaction.getSent() - transaction.getReceived();

                address.setSentCount(address.getSentCount() - 1);
                address.setSent(address.getSent() - amountSent);
                address.setBalance(address.getBalance() + amountSent);
                break;

            case RECEIVE:
            case COMMUNITY_FUND:
            case COMMUNITY_FUND_PAYOUT:
                Double amountReceived = transaction.getReceived() - transaction.getSent();

                address.setReceivedCount(address.getReceivedCount() - 1);
                address.setReceived(address.getReceived() - amountReceived);
                address.setBalance(address.getBalance() - amountReceived);
                break;

            case STAKING:
                Double amountStaked = transaction.getReceived() - transaction.getSent();

                address.setStakedCount(address.getStakedCount() - 1);
                address.setStakedSent(address.getStakedSent() - transaction.getSent());
                address.setStakedReceived(address.getStakedReceived() - transaction.getReceived());
                address.setStaked(address.getStaked() - amountStaked);
                address.setBalance(address.getBalance() - amountStaked);
                break;
        }

        AddressTransaction lastTransaction = addressTransactionService.getLastTransactionsForAddress(address.getHash());
        address.setBlockIndex(lastTransaction == null ? 0 : lastTransaction.getHeight());

        addressService.save(address);
        addressTransactionService.delete(transaction);

        applicationEventPublisher.publishEvent(new AddressRewindEvent(this, transaction));
    }
}
