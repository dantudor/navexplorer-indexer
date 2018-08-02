package com.navexplorer.indexer.coldstaking.rewinder;

import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.coldstaking.repository.ColdStakingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColdStakingRewinder {
    @Autowired
    ColdStakingRepository coldStakingRepository;

    public void rewindTransaction(AddressTransaction transaction) {
        coldStakingRepository.deleteAllByTransaction(transaction.getTransaction());
    }
}
