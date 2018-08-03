package com.navexplorer.indexer.block.rewinder;

import com.navexplorer.indexer.block.event.BlockRewindEvent;
import com.navexplorer.indexer.block.service.BlockIndexingActiveService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.navcoin.response.Transaction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BlockRewinderTest {
    @InjectMocks
    private BlockRewinder blockRewinder;

    @Mock
    private BlockIndexingActiveService blockIndexingActiveService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private NavcoinService navcoinService;

    @Mock
    private BlockTransactionService blockTransactionService;

    @Mock
    private BlockTransactionRepository blockTransactionRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void it_wont_rewind_top_10_blocks_if_indexing_is_disabled() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockRewinder.rewindTop10Blocks();

        verify(navcoinService, never()).getTransactionByHash(anyString());
    }

    @Test
    public void it_wont_rewind_to_missing_transactions_if_indexing_is_disabled() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockRewinder.rewindToMissingTransaction("HASH");

        verify(blockRepository, never()).findTop10ByOrderByHeightDesc();
    }

    @Test
    public void it_wont_rewind_to_height_if_indexing_is_disabled() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockRewinder.rewindToHeight(1000L);

        verify(blockRepository, never()).findFirstByOrderByHeightDesc();
    }

    @Test
    public void it_can_rewind_the_top_10_blocks() {
        Block block = new Block();
        List<Block> blocks = Arrays.asList(block, block, block, block, block, block, block, block, block, block);

        BlockTransaction blockTransaction = new BlockTransaction();
        List<BlockTransaction> blockTransactions = Arrays.asList(blockTransaction);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockRepository.findTop10ByOrderByHeightDesc()).thenReturn(blocks);
        when(blockTransactionService.getByHeight(anyLong())).thenReturn(blockTransactions);


        blockRewinder.rewindTop10Blocks();

        verify(blockTransactionRepository, times(blocks.size())).delete(blockTransactions);
        verify(blockRepository, times(blocks.size())).delete(block);
        verify(applicationEventPublisher, times(blocks.size())).publishEvent(any(BlockRewindEvent.class));
    }

    @Test
    public void it_can_rewind_to_a_target_height() {
        Long targetHeight = 10L;

        Block block1 = new Block();
        block1.setHeight(12L);

        Block block2 = new Block();
        block2.setHeight(11L);

        Block block3 = new Block();
        block3.setHeight(10L);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockRepository.findFirstByOrderByHeightDesc())
                .thenReturn(block1)
                .thenReturn(block2)
                .thenReturn(block3);

        blockRewinder.rewindToHeight(targetHeight);

        verify(blockTransactionRepository, times(2)).delete(anyListOf(BlockTransaction.class));
        verify(blockRepository, times(2)).delete(any(Block.class));
        verify(applicationEventPublisher, times(2)).publishEvent(any(BlockRewindEvent.class));
    }

    @Test
    public void it_can_rewind_to_a_missing_transaction() {
        String hash = "HASH";
        Transaction transaction = new Transaction();
        transaction.setHeight(100);

        Block block1 = new Block();
        block1.setHeight(101L);

        Block block2 = new Block();
        block2.setHeight(100L);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(navcoinService.getTransactionByHash(hash)).thenReturn(transaction);
        when(blockRepository.findFirstByOrderByHeightDesc())
                .thenReturn(block1)
                .thenReturn(block2);

        blockRewinder.rewindToMissingTransaction(hash);

        verify(blockTransactionRepository).delete(anyListOf(BlockTransaction.class));
        verify(blockRepository).delete(any(Block.class));
        verify(applicationEventPublisher).publishEvent(any(BlockRewindEvent.class));
    }
}
