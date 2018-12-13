package com.navexplorer.indexer.address.transition;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import com.navexplorer.indexer.address.event.AddressRewindEvent;
import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.entity.AddressTransactionType;
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
                transaction.setBalance(address.getBalance());

                transaction.setColdStakingBalance(address.getColdStakedBalance() + transaction.getColdStakingReceived() - transaction.getColdStakingSent());
                address.setColdStakedBalance(transaction.getColdStakingBalance());
                break;

            case RECEIVE:
            case COMMUNITY_FUND_PAYOUT:
                address.receive(transaction.getReceived() - transaction.getSent());
                transaction.setBalance(address.getBalance());

                transaction.setColdStakingBalance(address.getColdStakedBalance() + transaction.getColdStakingReceived() - transaction.getColdStakingSent());
                address.setColdStakedBalance(transaction.getColdStakingBalance());
                break;

            case STAKING:
            case COLD_STAKING:
                if (transaction.getColdStakingSent() != 0) {
                    address.coldStake(transaction.getColdStakingSent(), transaction.getColdStakingReceived());
                    transaction.setColdStakingBalance(address.getColdStakedBalance());
                } else {
                    address.stake(transaction.getSent(), transaction.getReceived());
                    transaction.setBalance(address.getBalance());
                }
                break;
        }


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

                transaction.setColdStakingBalance(address.getColdStakedBalance() - transaction.getColdStakingReceived());
                address.setColdStakedBalance(transaction.getColdStakingBalance());
                break;

            case RECEIVE:
            case COMMUNITY_FUND_PAYOUT:
                Double amountReceived = transaction.getReceived() - transaction.getSent();

                address.setReceivedCount(address.getReceivedCount() - 1);
                address.setReceived(address.getReceived() - amountReceived);
                address.setBalance(address.getBalance() - amountReceived);

                transaction.setColdStakingBalance(address.getColdStakedBalance() - transaction.getColdStakingReceived());
                address.setColdStakedBalance(transaction.getColdStakingBalance());
                break;

            case STAKING:
            case COLD_STAKING:
                if (transaction.getColdStakingSent() != 0) {
                    Double amountStaked = transaction.getColdStakingReceived() - transaction.getColdStakingSent();

                    address.setColdStakedCount(address.getColdStakedCount() - 1);
                    address.setColdStakedSent(address.getColdStakedSent() - transaction.getColdStakingSent());
                    address.setColdStakedReceived(address.getColdStakedReceived() - transaction.getColdStakingReceived());
                    address.setColdStaked(address.getColdStaked() - amountStaked);
                    address.setColdStakedBalance(address.getColdStakedBalance() - amountStaked);
                } else {
                    Double amountStaked = transaction.getReceived() - transaction.getSent();

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
