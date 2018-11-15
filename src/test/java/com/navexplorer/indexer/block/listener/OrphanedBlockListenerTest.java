package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.block.event.OrphanedBlockEvent;
import com.navexplorer.indexer.block.rewinder.BlockRewinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OrphanedBlockListenerTest {
    @InjectMocks
    private OrphanedBlockListener orphanedBlockListener;

    @Mock
    private BlockRewinder blockRewinder;

    @Test
    public void it_will_trigger_a_10_block_rewind() {
        orphanedBlockListener.onApplicationEvent(new OrphanedBlockEvent(new Object()));

        verify(blockRewinder).rewindTop10Blocks();
    }
}
