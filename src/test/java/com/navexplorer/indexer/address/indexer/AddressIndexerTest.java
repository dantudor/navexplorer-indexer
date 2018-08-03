package com.navexplorer.indexer.address.indexer;

import com.navexplorer.indexer.address.factory.AddressTransactionFactory;
import com.navexplorer.indexer.address.transition.AddressTransition;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.service.BlockTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class AddressIndexerTest {
    @InjectMocks
    private AddressIndexer addressIndexer;

    @Mock
    private BlockTransactionService blockTransactionService;

    @Mock
    private AddressExtractor addressExtractor;

    @Mock
    private AddressTransactionFactory addressTransactionFactory;

    @Mock
    private AddressTransition addressTransition;

    @Test
    public void it_will_ignore_null_transactions() {
        Block block = new Block();

        List<BlockTransaction> blockTransactions = new ArrayList<>();
        blockTransactions.add(new BlockTransaction());

        Set<String> addresses = new HashSet<>();
        addresses.add("Test Address 1");

        when(blockTransactionService.getByHeight(block.getHeight())).thenReturn(blockTransactions);
        when(addressExtractor.getAllAddressesFromBlockTransaction(blockTransactions.get(0))).thenReturn(addresses);
        when(addressTransactionFactory.create("Test Address 1", blockTransactions.get(0))).thenReturn(null);

        addressIndexer.indexBlock(block);

        verify(addressTransition, never()).up(any());
    }

    @Test
    public void it_will_transition_up_for_multiple_block_transactions() {
        Block block = new Block();

        BlockTransaction transaction1 = new BlockTransaction();
        transaction1.setHash("TRANSACTION_1");

        BlockTransaction transaction2 = new BlockTransaction();
        transaction2.setHash("TRANSACTION_2");

        List<BlockTransaction> blockTransactions = new ArrayList<>();
        blockTransactions.add(transaction1);
        blockTransactions.add(transaction2);

        when(blockTransactionService.getByHeight(block.getHeight())).thenReturn(blockTransactions);
        when(addressExtractor.getAllAddressesFromBlockTransaction(any())).thenReturn(new HashSet<>());

        addressIndexer.indexBlock(block);

        InOrder inOrder = inOrder(addressExtractor, addressExtractor);
        inOrder.verify(addressExtractor).getAllAddressesFromBlockTransaction(transaction1);
        inOrder.verify(addressExtractor).getAllAddressesFromBlockTransaction(transaction2);
    }

    public void it_will_transition_up_all_address_transactions_found() {
        Block block = new Block();

        List<BlockTransaction> blockTransactions = new ArrayList<>();
        blockTransactions.add(new BlockTransaction());

        Set<String> addresses = new HashSet<>();
        addresses.add("Test Address 1");
        addresses.add("Test Address 2");

        AddressTransaction addressTransaction1 = new AddressTransaction();
        AddressTransaction addressTransaction2 = new AddressTransaction();

        when(blockTransactionService.getByHeight(block.getHeight())).thenReturn(blockTransactions);
        when(addressExtractor.getAllAddressesFromBlockTransaction(blockTransactions.get(0))).thenReturn(addresses);
        when(addressTransactionFactory.create("Test Address 1", blockTransactions.get(0))).thenReturn(addressTransaction1);
        when(addressTransactionFactory.create("Test Address 2", blockTransactions.get(0))).thenReturn(addressTransaction2);

        addressIndexer.indexBlock(block);

        InOrder inOrder = inOrder(addressTransition, addressTransition);
        inOrder.verify(addressTransition).up(addressTransaction1);
        inOrder.verify(addressTransition).up(addressTransaction2);
    }
}
