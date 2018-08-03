package com.navexplorer.indexer.address.event;

import com.navexplorer.library.address.entity.AddressTransaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class AddressRewindEvent extends ApplicationEvent {
    @Getter
    private AddressTransaction transaction;

    public AddressRewindEvent(Object source, AddressTransaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
