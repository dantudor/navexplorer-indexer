package com.navexplorer.indexer.address.listener;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import com.navexplorer.indexer.coldstaking.indexer.ColdStakingIndexer;
import com.navexplorer.library.address.entity.AddressTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AddressIndexedListener implements ApplicationListener<AddressIndexedEvent> {
    @Autowired
    ColdStakingIndexer coldStakingIndexer;

    @Override
    public void onApplicationEvent(AddressIndexedEvent event) {
        AddressTransaction transaction = event.getTransaction();

        if (transaction.getColdStaking() != null && transaction.getColdStaking()) {
            coldStakingIndexer.indexTransaction(transaction);
        }
    }
}
