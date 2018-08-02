package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.event.BlockIndexedEvent;
import com.navexplorer.indexer.block.event.OrphanedBlockEvent;
import com.navexplorer.indexer.block.exception.BlockIndexingNotActiveException;
import com.navexplorer.indexer.block.exception.CreateBlockException;
import com.navexplorer.indexer.block.exception.ReachedBestBlockException;
import com.navexplorer.indexer.block.service.BlockIndexingActiveService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
    private BlockTransactionService blockTransactionService;

    @Mock
    private BlockTransactionRepository blockTransactionRepository;

    @Mock
    private BlockTransactionIndexer blockTransactionIndexer;

    @Test(expected = BlockIndexingNotActiveException.class)
    public void indexBlocksThrowsExceptionWhenNotActive() {
        when(blockIndexingActiveService.isActive()).thenReturn(false);

        blockIndexer.indexBlocks();
    }

    @Test(expected = ReachedBestBlockException.class)
    public void indexBlocksStopsWhenNoNewBlocksAreFound() {
        Block bestBlock = new Block();
        bestBlock.setHeight(10L);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(bestBlock.getHeight() + 1)).thenReturn(null);

        blockIndexer.indexBlocks();
    }

    @Test
    public void indexBlocksFiresOrphanEventWhenDetected() {
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
    }

    @Test(expected = CreateBlockException.class)
    public void indexBlockDoesNotFireOrphanEventOnFirstBlock() {
        Block bestBlock = null;

        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setPreviousblockhash("PREVIOUS_BLOCK_HASH");

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(1L)).thenReturn(apiBlock);

        blockIndexer.indexBlocks();
    }

    @Test
    public void indexBlockSuccess() {
        Block bestBlock = new Block();
        bestBlock.setHeight(50L);
        bestBlock.setHash("BEST_BLOCK_HASH");

        org.navcoin.response.Block apiBlock = getStubApiBlock();
        apiBlock.setPreviousblockhash("BEST_BLOCK_HASH");

        BlockTransaction blockTransaction1 = mock(BlockTransaction.class);
        when(blockTransaction1.getFees()).thenReturn(10000.00);
        when(blockTransaction1.isSpend()).thenReturn(true);
        when(blockTransaction1.getOutputAmount()).thenReturn(500000.00);

        BlockTransaction blockTransaction2 = mock(BlockTransaction.class);
        when(blockTransaction2.getFees()).thenReturn(0.00);
        when(blockTransaction2.isSpend()).thenReturn(false);
        when(blockTransaction2.getOutputAmount()).thenReturn(500000.00);

        BlockTransaction blockTransaction3 = mock(BlockTransaction.class);
        when(blockTransaction3.getFees()).thenReturn(10000.00);
        when(blockTransaction3.isSpend()).thenReturn(false);
        when(blockTransaction3.getOutputAmount()).thenReturn(500000.00);

        List<BlockTransaction> blockTransactions = new ArrayList<BlockTransaction>() {{
            add(blockTransaction1); add(blockTransaction2); add(blockTransaction3);
        }};

        BlockTransaction stakingTransaction = new BlockTransaction();
        stakingTransaction.setStake(55555.00);
        Output output = new Output();
        output.setAddresses(new ArrayList<String>() {{add("STAKING_ADDRESS");}});
        List<Output> outputs = new ArrayList<Output>() {{add(output);}};
        stakingTransaction.setOutputs(outputs);

        when(blockIndexingActiveService.isActive()).thenReturn(true);
        when(blockService.getBestBlock()).thenReturn(bestBlock);
        when(navcoinService.getBlockByHeight(bestBlock.getHeight() + 1)).thenReturn(apiBlock);
        when(blockTransactionService.getByHeight(apiBlock.getHeight())).thenReturn(blockTransactions);
        when(blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(apiBlock.getHash(), 0.0)).thenReturn(stakingTransaction);

        Block block = blockIndexer.indexBlocks();

        verify(applicationEventPublisher).publishEvent(any(BlockIndexedEvent.class));

        assertThat(block.getHash()).isEqualTo(apiBlock.getHash());
        assertThat(block.getMerkleRoot()).isEqualTo(apiBlock.getMerkleroot());
        assertThat(block.getBits()).isEqualTo(apiBlock.getBits());
        assertThat(block.getSize()).isEqualTo(apiBlock.getSize());
        assertThat(block.getVersion()).isEqualTo(apiBlock.getVersion());
        assertThat(block.getVersionHex()).isEqualTo(apiBlock.getVersionHex());
        assertThat(block.getNonce()).isEqualTo(apiBlock.getNonce());
        assertThat(block.getHeight()).isEqualTo(apiBlock.getHeight());
        assertThat(block.getDifficulty()).isEqualTo(apiBlock.getDifficulty());
        assertThat(block.getConfirmations()).isEqualTo(apiBlock.getConfirmations());
        assertThat(block.getStake()).isEqualTo(55555.00);
        assertThat(block.getStakedBy()).isEqualTo("STAKING_ADDRESS");
        assertThat(block.getFees()).isEqualTo(20000.00);
        assertThat(block.getSpend()).isEqualTo(500000.00);
        assertThat(block.getTransactions()).isEqualTo(blockTransactions.size());
    }

    private org.navcoin.response.Block getStubApiBlock() {
        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setHash("BLOCK_HASH");
        apiBlock.setMerkleroot("BLOCK_MERKLE_ROOT");
        apiBlock.setBits("BLOCK_BITS");
        apiBlock.setSize(700L);
        apiBlock.setVersion(1000L);
        apiBlock.setVersionHex("BLOCK_VERSION_HEX");
        apiBlock.setNonce(50000L);
        apiBlock.setHeight(1L);
        apiBlock.setDifficulty(10.0);
        apiBlock.setConfirmations(350L);
        apiBlock.setTime(73647362L);
        apiBlock.setTx(new ArrayList<String>() {{
            add("TRANSACTION_A");
            add("TRANSACTION_B");
            add("TRANSACTION_C");
        }});

        return apiBlock;
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }
}
