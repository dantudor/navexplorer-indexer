package com.navexplorer.indexer.address.transition;

import com.navexplorer.indexer.address.event.AddressIndexedEvent;
import com.navexplorer.indexer.address.event.AddressRewindEvent;
import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.entity.AddressTransactionType;
import com.navexplorer.library.address.service.AddressService;
import com.navexplorer.library.address.service.AddressTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class AddressTransitionTest {
    @InjectMocks
    private AddressTransition addressTransition;

    @Mock
    private AddressService addressService;

    @Mock
    private AddressTransactionService addressTransactionService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void it_can_transition_a_send_address_up() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.SEND);
        transaction.setSent(10000.00);
        transaction.setReceived(5000.00);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20000.00);
        address.setSentCount(5);
        address.setSent(750.00);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);

        addressTransition.up(transaction);

        assertThat(address.getSentCount()).isEqualTo(6);
        assertThat(address.getSent()).isEqualTo(5750.00);
        assertThat(address.getBalance()).isEqualTo(15000.00);
        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
        assertThat(transaction.getBalance()).isEqualTo(15000.00);

        verify(addressService).save(address);
        verify(addressTransactionService).save(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressIndexedEvent.class));
    }

    @Test
    public void it_can_transition_a_send_address_down() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.SEND);
        transaction.setSent(10000.00);
        transaction.setReceived(5000.00);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(15000.00);
        address.setSentCount(6);
        address.setSent(5750.00);

        AddressTransaction lastTransaction = new AddressTransaction();
        lastTransaction.setHeight(500);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);
        when(addressTransactionService.getLastTransactionsForAddress(hash)).thenReturn(lastTransaction);

        addressTransition.down(transaction);

        assertThat(address.getSentCount()).isEqualTo(5);
        assertThat(address.getSent()).isEqualTo(750.00);
        assertThat(address.getBalance()).isEqualTo(20000.00);
        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());

        verify(addressService).save(address);
        verify(addressTransactionService).delete(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressRewindEvent.class));
    }

    @Test
    public void it_can_transition_a_receive_address_up() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.RECEIVE);
        transaction.setSent(5000.00);
        transaction.setReceived(10000.00);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20000.00);
        address.setReceivedCount(5);
        address.setReceived(750.00);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);

        addressTransition.up(transaction);

        assertThat(address.getReceivedCount()).isEqualTo(6);
        assertThat(address.getReceived()).isEqualTo(5750.00);
        assertThat(address.getBalance()).isEqualTo(25000.00);
        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
        assertThat(transaction.getBalance()).isEqualTo(25000.00);

        verify(addressService).save(address);
        verify(addressTransactionService).save(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressIndexedEvent.class));
    }

    @Test
    public void it_can_transition_a_receive_address_down() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.RECEIVE);
        transaction.setSent(5000.00);
        transaction.setReceived(10000.00);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(15000.00);
        address.setReceivedCount(6);
        address.setReceived(5750.00);

        AddressTransaction lastTransaction = new AddressTransaction();
        lastTransaction.setHeight(500);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);
        when(addressTransactionService.getLastTransactionsForAddress(hash)).thenReturn(lastTransaction);

        addressTransition.down(transaction);

        assertThat(address.getReceivedCount()).isEqualTo(5);
        assertThat(address.getReceived()).isEqualTo(750.00);
        assertThat(address.getBalance()).isEqualTo(10000.00);
        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());

        verify(addressService).save(address);
        verify(addressTransactionService).delete(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressRewindEvent.class));
    }

    @Test
    public void it_can_transition_the_community_fund_address_up() {
        String hash = "Community Fund";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.COMMUNITY_FUND);
        transaction.setSent(0.0);
        transaction.setReceived(0.25);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20000.00);
        address.setReceivedCount(5);
        address.setReceived(750.00);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);

        addressTransition.up(transaction);

        assertThat(address.getReceivedCount()).isEqualTo(6);
        assertThat(address.getReceived()).isEqualTo(750.25);
        assertThat(address.getBalance()).isEqualTo(20000.25);
        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
        assertThat(transaction.getBalance()).isEqualTo(20000.25);

        verify(addressService).save(address);
        verify(addressTransactionService).save(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressIndexedEvent.class));
    }

    @Test
    public void it_can_transition_the_community_fund_address_down() {
        String hash = "Community Fund";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.RECEIVE);
        transaction.setSent(0.0);
        transaction.setReceived(0.25);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20000.25);
        address.setReceivedCount(6);
        address.setReceived(750.25);

        AddressTransaction lastTransaction = new AddressTransaction();
        lastTransaction.setHeight(500);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);
        when(addressTransactionService.getLastTransactionsForAddress(hash)).thenReturn(lastTransaction);

        addressTransition.down(transaction);

        assertThat(address.getReceivedCount()).isEqualTo(5);
        assertThat(address.getReceived()).isEqualTo(750.00);
        assertThat(address.getBalance()).isEqualTo(20000.00);
        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());

        verify(addressService).save(address);
        verify(addressTransactionService).delete(transaction);
        verify(applicationEventPublisher).publishEvent(any(AddressRewindEvent.class));
    }

    @Test
    public void it_can_transition_a_staking_address_up() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.STAKING);
        transaction.setSent(200.0);
        transaction.setReceived(220.0);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20000.00);
        address.setStakedCount(5);
        address.setStakedSent(1000.00);
        address.setStakedReceived(1500.00);
        address.setStaked(500.00);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);

//        addressTransition.up(transaction);
//
//        assertThat(address.getStakedCount()).isEqualTo(6);
//        assertThat(address.getStakedSent()).isEqualTo(1200.00);
//        assertThat(address.getStakedReceived()).isEqualTo(1720);
//        assertThat(address.getStaked()).isEqualTo(520.00);
//        assertThat(address.getBalance()).isEqualTo(20020.00);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//        assertThat(transaction.getBalance()).isEqualTo(20020.00);
//
//        verify(addressService).save(address);
//        verify(addressTransactionService).save(transaction);
//        verify(applicationEventPublisher).publishEvent(any(AddressIndexedEvent.class));
    }

    @Test
    public void it_can_transition_a_staking_address_down() {
        String hash = "TEST ADDRESS";
        Integer height = 1000;

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(hash);
        transaction.setHeight(height);
        transaction.setType(AddressTransactionType.STAKING);
        transaction.setSent(200.0);
        transaction.setReceived(220.0);

        Address address = new Address();
        address.setHash(hash);
        address.setBalance(20020.00);
        address.setStakedCount(6);
        address.setStakedSent(1200.00);
        address.setStakedReceived(1720.00);
        address.setStaked(520.00);

        AddressTransaction lastTransaction = new AddressTransaction();
        lastTransaction.setHeight(500);

        when(addressService.getAddress(transaction.getAddress())).thenReturn(address);
        when(addressTransactionService.getLastTransactionsForAddress(hash)).thenReturn(lastTransaction);

//        addressTransition.down(transaction);
//
//        assertThat(address.getStakedCount()).isEqualTo(5);
//        assertThat(address.getStakedSent()).isEqualTo(1000.00);
//        assertThat(address.getStakedReceived()).isEqualTo(1500.00);
//        assertThat(address.getStaked()).isEqualTo(500.00);
//        assertThat(address.getBalance()).isEqualTo(20000.00);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//
//        verify(addressService).save(address);
//        verify(addressTransactionService).delete(transaction);
//        verify(applicationEventPublisher).publishEvent(any(AddressRewindEvent.class));
    }
}
