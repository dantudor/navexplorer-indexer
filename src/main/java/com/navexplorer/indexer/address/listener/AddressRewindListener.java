package com.navexplorer.indexer.address.listener;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import com.navexplorer.indexer.coldstaking.rewinder.ColdStakingRewinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AddressRewindListener implements ApplicationListener<AddressIndexedEvent> {
    @Autowired
    ColdStakingRewinder coldStakingRewinder;

    @Override
    public void onApplicationEvent(AddressIndexedEvent event) {
        if (event.getTransaction().getColdStaking()) {
            coldStakingRewinder.rewindTransaction(event.getTransaction());
        }
    }
}
