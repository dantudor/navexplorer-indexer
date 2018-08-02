package com.navexplorer.indexer.address.transition;

//import com.navexplorer.library.address.entity.*;
//import com.navexplorer.library.address.service.AddressService;
//import com.navexplorer.library.address.service.AddressTransactionService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.mockito.Mockito.*;
//import static org.assertj.core.api.Assertions.*;

//@RunWith(SpringRunner.class)
public class SpendingTransitionTest {
//    @InjectMocks
//    private SpendingTransition transition;
//
//    @Mock
//    private AddressTransactionService addressTransactionService;
//
//    @Mock
//    private AddressService addressService;
//
//    @Test
//    public void testUpReceiveTransaction() {
//        SpendingTransaction transaction = new SpendingTransaction();
//        transaction.setType(AddressTransactionType.RECEIVE);
//        transaction.setHeight(30);
//        transaction.setSent(1000.0);
//        transaction.setReceived(6000.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setReceivedCount(2);
//        address.setReceived(10000.0);
//        address.setBalance(10000.0);
//
//        transition.up(transaction, address);
//
//        verify(addressTransactionService).save(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getReceivedCount()).isEqualTo(3);
//        assertThat(address.getReceived()).isEqualTo(15000.0);
//        assertThat(address.getBalance()).isEqualTo(15000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//
//        assertThat(transaction.getBalance()).isEqualTo(15000);
//    }
//
//    @Test
//    public void testDownReceiveTransaction() {
//        SpendingTransaction transaction = new SpendingTransaction();
//        transaction.setType(AddressTransactionType.RECEIVE);
//        transaction.setHeight(30);
//        transaction.setSent(1000.0);
//        transaction.setReceived(6000.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setReceivedCount(3);
//        address.setReceived(15000.0);
//        address.setBalance(15000.0);
//
//        AddressTransaction lastTransaction = new SpendingTransaction();
//        lastTransaction.setHeight(29);
//        when(addressTransactionService.getLastTransactionsForAddress(address.getHash())).thenReturn(lastTransaction);
//
//        transition.down(transaction, address);
//
//        verify(addressTransactionService).delete(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getReceivedCount()).isEqualTo(2);
//        assertThat(address.getReceived()).isEqualTo(10000.0);
//        assertThat(address.getBalance()).isEqualTo(10000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//    }
//
//    @Test
//    public void testUpSentTransaction() {
//        SpendingTransaction transaction = new SpendingTransaction();
//        transaction.setType(AddressTransactionType.SEND);
//        transaction.setHeight(30);
//        transaction.setSent(500.0);
//        transaction.setReceived(100.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setSentCount(2);
//        address.setSent(1000.0);
//        address.setBalance(9000.0);
//
//        transition.up(transaction, address);
//
//        verify(addressTransactionService).save(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getSentCount()).isEqualTo(3);
//        assertThat(address.getSent()).isEqualTo(1400.0);
//        assertThat(address.getBalance()).isEqualTo(8600.0);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//
//        assertThat(transaction.getBalance()).isEqualTo(8600.0);
//    }
//
//    @Test
//    public void testDownSentTransaction() {
//        SpendingTransaction transaction = new SpendingTransaction();
//        transaction.setType(AddressTransactionType.SEND);
//        transaction.setHeight(30);
//        transaction.setSent(500.0);
//        transaction.setReceived(100.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setSentCount(2);
//        address.setSent(1400.0);
//        address.setBalance(8600.0);
//
//        AddressTransaction lastTransaction = new SpendingTransaction();
//        lastTransaction.setHeight(29);
//        when(addressTransactionService.getLastTransactionsForAddress(address.getHash())).thenReturn(lastTransaction);
//
//        transition.down(transaction, address);
//
//        verify(addressTransactionService).delete(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getSentCount()).isEqualTo(1);
//        assertThat(address.getSent()).isEqualTo(1000.0);
//        assertThat(address.getBalance()).isEqualTo(9000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//    }
}
