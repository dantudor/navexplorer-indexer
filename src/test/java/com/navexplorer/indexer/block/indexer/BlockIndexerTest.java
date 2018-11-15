package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.event.BlockIndexedEvent;
import com.navexplorer.indexer.block.event.OrphanedBlockEvent;
import com.navexplorer.indexer.block.exception.BlockIndexingNotActiveException;
import com.navexplorer.indexer.block.exception.ReachedBestBlockException;
import com.navexplorer.indexer.block.factory.BlockFactory;
import com.navexplorer.indexer.block.service.BlockIndexingActiveService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
public class BlockIndexerTest {
    @InjectMocks
    private BlockIndexer blockIndexer;

    @Mock
    private BlockIndexingActiveService blockIndexingActiveService;

    @Mock
    private BlockService blockService;

    @Mock
    private NavcoinService navcoinService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private BlockFactory blockFactory;

    @Mock
    private BlockTransactionService blockTransactionService;

    @Mock
    private BlockTransactionRepository blockTransactionRepository;

    @Mock
    private BlockTransactionIndexer blockTransactionIndexer;

    @Test(expected = BlockIndexingNotActiveException.class)
    public void it_will_take_no_action_if_indexing_is_disabled() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockIndexer.indexBlocks();

        verify(blockService, never()).getBestBlock();
    }

    @Test(expected = ReachedBestBlockException.class)
    public void it_stops_indexing_when_the_best_block_is_reached() {
        Block bestBlock = new Block();
        bestBlock.setHeight(10L);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(bestBlock.getHeight() + 1)).thenReturn(null);

        blockIndexer.indexBlocks();
    }

    @Test
    public void it_will_stop_and_publish_an_event_when_an_orphan_block_is_found() {
        Block bestBlock = new Block();
        bestBlock.setHeight(50L);
        bestBlock.setHash("BEST_BLOCK_HASH");

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(bestBlock.getHeight() + 1)).thenReturn(apiBlock);

        blockIndexer.indexBlocks();

        verify(applicationEventPublisher).publishEvent(any(OrphanedBlockEvent.class));
        verify(blockFactory, never()).createBlock(apiBlock);
    }

    @Test
    public void it_will_not_consider_the_first_block_an_orphan() {
        Block bestBlock = null;

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");
        apiBlock.setTx(new ArrayList<>());

        Block newBlock = new Block();
        newBlock.setHash("NEW HASH");

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(1L)).thenReturn(apiBlock);
        when(blockFactory.createBlock(apiBlock)).thenReturn(newBlock);
        when(blockTransactionService.getByHeight(anyLong())).thenReturn(new ArrayList<>());
        when(blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(newBlock.getHash(), 0.0)).thenReturn(null);

        blockIndexer.indexBlocks();

        verify(blockService, times(2)).save(newBlock);
        verify(applicationEventPublisher).publishEvent(any(BlockIndexedEvent.class));
    }

    @Test
    public void it_can_index_the_transactions_in_the_block() {
        Block bestBlock = null;

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");
        apiBlock.setTx(Arrays.asList("TX1", "TX2"));

        Block newBlock = new Block();
        newBlock.setHash("NEW HASH");

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(1L)).thenReturn(apiBlock);
        when(blockFactory.createBlock(apiBlock)).thenReturn(newBlock);
        when(blockTransactionService.getByHeight(anyLong())).thenReturn(new ArrayList<>());
        when(blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(newBlock.getHash(), 0.0)).thenReturn(null);

        blockIndexer.indexBlocks();

        verify(blockTransactionIndexer, times(2)).indexTransaction(anyString());
    }

    @Test
    public void it_can_update_fees_and_spend_properties() {
        Block bestBlock = null;

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");
        apiBlock.setTx(Arrays.asList());

        Block newBlock = new Block();
        newBlock.setHash("NEW HASH");

        Output output = new Output();
        output.setAmount(500.0);

        BlockTransaction transaction = new BlockTransaction();
        transaction.setFees(1000.0);
        transaction.setType(BlockTransactionType.SPEND);
        transaction.setOutputs(Arrays.asList(output));
        List<BlockTransaction> transactions = Arrays.asList(transaction);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(1L)).thenReturn(apiBlock);
        when(blockFactory.createBlock(apiBlock)).thenReturn(newBlock);
        when(blockTransactionService.getByHeight(anyLong())).thenReturn(transactions);
        when(blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(newBlock.getHash(), 0.0)).thenReturn(null);

        blockIndexer.indexBlocks();

        assertThat(newBlock.getFees()).isEqualTo(transaction.getFees());
        assertThat(newBlock.getSpend()).isEqualTo(transaction.getOutputAmount());
    }


    @Test
    public void it_can_update_staking_properties() {
        Block bestBlock = null;

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");
        apiBlock.setTx(Arrays.asList());

        Block newBlock = new Block();
        newBlock.setHash("NEW HASH");

        Output output1 = new Output();
        output1.setAmount(0.0);

        Output output2 = new Output();
        output2.setAmount(0.0);
        output2.setAddresses(Arrays.asList("ADDRESS_1"));

        BlockTransaction transaction = new BlockTransaction();
        transaction.setStake(100.00);
        transaction.setOutputs(Arrays.asList(output1, output2));

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(1L)).thenReturn(apiBlock);
        when(blockFactory.createBlock(apiBlock)).thenReturn(newBlock);
        when(blockTransactionService.getByHeight(anyLong())).thenReturn(new ArrayList<>());
        when(blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(newBlock.getHash(), 0.0)).thenReturn(transaction);

        blockIndexer.indexBlocks();

        assertThat(newBlock.getStake()).isEqualTo(transaction.getStake());
        assertThat(newBlock.getStakedBy()).isEqualTo(output2.getAddresses().get(0));
    }

    @Test
    public void it_will_stop_indexing_all_blocks_on_exception() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockIndexer.indexAllBlocks();

        verify(blockIndexingActiveService).isActive();
    }
}
