package com.navexplorer.indexer.address.listener;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AddressRewindListener implements ApplicationListener<AddressIndexedEvent> {
    @Override
    public void onApplicationEvent(AddressIndexedEvent event) {
        //
    }
}
