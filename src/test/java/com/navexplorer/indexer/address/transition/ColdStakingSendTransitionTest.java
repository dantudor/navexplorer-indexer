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
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class)
public class ColdStakingSendTransitionTest {
//    @InjectMocks
//    private ColdStakingSendTransition transition;
//
//    @Mock
//    private AddressTransactionService addressTransactionService;
//
//    @Mock
//    private AddressService addressService;
//
//    @Test
//    public void testUpSpender() {
//        ColdStakingTransaction transaction = new ColdStakingTransaction();
//        transaction.setType(AddressTransactionType.COLD_STAKING_SEND);
//        transaction.setHeight(30);
//        transaction.setSent(5000.0);
//        transaction.setReceived(10000.0);
//        transaction.setSpendingHash("SPENDING HASH");
//        transaction.setStakingHash("STAKING HASH");
//
//        Address address = new Address();
//        address.setHash(transaction.getSpendingHash());
//        address.setBalance(100000.0);
//        address.setColdStakedBalance(10000.0);
//        address.setReceivedCount(10);
//        address.setReceived(120000.0);
//        address.setSentCount(2);
//        address.setSent(4500.0);
//
//        transition.up(transaction, address);
//
//        verify(addressTransactionService).save(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getReceivedCount()).isEqualTo(11);
//        assertThat(address.getReceived()).isEqualTo(125000.0);
//        assertThat(address.getBalance()).isEqualTo(105000.0);
//        assertThat(address.getColdStakedBalance()).isEqualTo(10000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//        assertThat(address.getSentCount()).isEqualTo(2);
//        assertThat(address.getSent()).isEqualTo(4500.0);
//
//        assertThat(transaction.getBalance()).isEqualTo(105000.0);
//    }
//
//    @Test
//    public void testDownSpender() {
//        ColdStakingTransaction transaction = new ColdStakingTransaction();
//        transaction.setType(AddressTransactionType.COLD_STAKING_SEND);
//        transaction.setHeight(30);
//        transaction.setSent(5000.0);
//        transaction.setReceived(10000.0);
//        transaction.setSpendingHash("SPENDING HASH");
//        transaction.setStakingHash("STAKING HASH");
//
//        Address address = new Address();
//        address.setHash(transaction.getSpendingHash());
//        address.setBalance(105000.0);
//        address.setColdStakedBalance(10000.0);
//        address.setReceivedCount(11);
//        address.setReceived(125000.0);
//        address.setSentCount(2);
//        address.setSent(4500.0);
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
//        assertThat(address.getReceivedCount()).isEqualTo(10);
//        assertThat(address.getReceived()).isEqualTo(120000.0);
//        assertThat(address.getBalance()).isEqualTo(100000.0);
//        assertThat(address.getColdStakedBalance()).isEqualTo(10000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//        assertThat(address.getSentCount()).isEqualTo(2);
//        assertThat(address.getSent()).isEqualTo(4500.0);
//    }
//
//    @Test
//    public void testUpStaker() {
//        ColdStakingTransaction transaction = new ColdStakingTransaction();
//        transaction.setType(AddressTransactionType.COLD_STAKING_SEND);
//        transaction.setHeight(30);
//        transaction.setSent(5000.0);
//        transaction.setReceived(5000.0);
//        transaction.setSpendingHash("SPENDING HASH");
//        transaction.setStakingHash("STAKING HASH");
//
//        Address address = new Address();
//        address.setHash(transaction.getStakingHash());
//        address.setBalance(100000.0);
//        address.setColdStakedBalance(10000.0);
//        address.setSentCount(10);
//        address.setSent(120000.0);
//
//        transition.up(transaction, address);
//
//        verify(addressTransactionService).save(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getSentCount()).isEqualTo(11);
//        assertThat(address.getSent()).isEqualTo(125000.0);
//        assertThat(address.getBalance()).isEqualTo(95000.0);
//        assertThat(address.getColdStakedBalance()).isEqualTo(15000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//
//        assertThat(transaction.getBalance()).isEqualTo(95000.0);
//    }
//
//    @Test
//    public void testDownStaker() {
//        ColdStakingTransaction transaction = new ColdStakingTransaction();
//        transaction.setType(AddressTransactionType.COLD_STAKING_SEND);
//        transaction.setHeight(30);
//        transaction.setSent(5000.0);
//        transaction.setReceived(5000.0);
//        transaction.setSpendingHash("SPENDING HASH");
//        transaction.setStakingHash("STAKING HASH");
//
//        Address address = new Address();
//        address.setHash(transaction.getStakingHash());
//        address.setBalance(95000.0);
//        address.setColdStakedBalance(15000.0);
//        address.setSentCount(11);
//        address.setSent(125000.0);
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
//        assertThat(address.getSentCount()).isEqualTo(10);
//        assertThat(address.getSent()).isEqualTo(120000.0);
//        assertThat(address.getBalance()).isEqualTo(100000.0);
//        assertThat(address.getColdStakedBalance()).isEqualTo(10000.0);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//    }
}
