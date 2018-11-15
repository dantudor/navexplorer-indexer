package com.navexplorer.indexer.address.event;

import com.navexplorer.library.address.entity.AddressTransaction;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressIndexedEventTest {
    @Test
    public void it_takes_an_address_transaction() {
        AddressTransaction transaction = new AddressTransaction();
        AddressIndexedEvent event = new AddressIndexedEvent(new Object(), transaction);

        assertThat(event.getTransaction()).isEqualTo(transaction);
    }
}
