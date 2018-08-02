package com.navexplorer.indexer.address.transition;

//import com.navexplorer.library.address.entity.Address;
//import com.navexplorer.library.address.entity.AddressTransaction;
//import com.navexplorer.library.address.entity.StakingTransaction;
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
public class StakingTransitionTest {
//    @InjectMocks
//    private StakingTransition transition;
//
//    @Mock
//    private AddressTransactionService addressTransactionService;
//
//    @Mock
//    private AddressService addressService;
//
//    @Test
//    public void testUp() {
//        StakingTransaction transaction = new StakingTransaction();
//        transaction.setHeight(30);
//        transaction.setSent(2000.0);
//        transaction.setReceived(2200.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setStakedCount(10);
//        address.setStakedSent(5000.0);
//        address.setStaked(7500.0);
//        address.setBalance(2500.0);
//
//        transition.up(transaction, address);
//
//        verify(addressTransactionService).save(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getStakedCount()).isEqualTo(11);
//        assertThat(address.getStakedSent()).isEqualTo(7000);
//        assertThat(address.getStaked()).isEqualTo(9700);
//        assertThat(address.getBalance()).isEqualTo(2700);
//        assertThat(address.getBlockIndex()).isEqualTo(transaction.getHeight());
//
//        assertThat(transaction.getBalance()).isEqualTo(2700);
//    }
//
//    @Test
//    public void testDown() {
//        StakingTransaction transaction = new StakingTransaction();
//        transaction.setHeight(30);
//        transaction.setSent(2000.0);
//        transaction.setReceived(2200.0);
//
//        Address address = new Address();
//        address.setHash("ADDRESS HASH");
//        address.setStakedCount(11);
//        address.setStakedSent(7000.0);
//        address.setStaked(9700.0);
//        address.setBalance(2700.0);
//
//        AddressTransaction lastTransaction = new StakingTransaction();
//        lastTransaction.setHeight(29);
//        when(addressTransactionService.getLastTransactionsForAddress(address.getHash())).thenReturn(lastTransaction);
//
//        transition.down(transaction, address);
//
//        verify(addressTransactionService).delete(transaction);
//        verify(addressService).save(address);
//
//        assertThat(address.getStakedCount()).isEqualTo(10);
//        assertThat(address.getStakedSent()).isEqualTo(5000);
//        assertThat(address.getStaked()).isEqualTo(7500);
//        assertThat(address.getBalance()).isEqualTo(2500);
//        assertThat(address.getBlockIndex()).isEqualTo(lastTransaction.getHeight());
//    }
}
