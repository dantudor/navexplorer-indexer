package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.address.indexer.AddressIndexer;
import com.navexplorer.indexer.block.event.BlockIndexedEvent;
import com.navexplorer.indexer.block.indexer.SignalIndexer;
import com.navexplorer.library.block.entity.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BlockIndexedListenerTest {
    @InjectMocks
    private BlockIndexedListener blockIndexedListener;

    @Mock
    private SignalIndexer signalIndexer;

    @Mock
    private AddressIndexer addressIndexer;

    @Test
    public void it_will_trigger_the_signal_indexer() {
        Block block = new Block();

        blockIndexedListener.onApplicationEvent(new BlockIndexedEvent(new Object(), block));

        verify(signalIndexer).indexBlock(block);
    }

    @Test
    public void it_will_trigger_the_address_indexer() {
        Block block = new Block();

        blockIndexedListener.onApplicationEvent(new BlockIndexedEvent(new Object(), block));

        verify(addressIndexer).indexBlock(block);
    }
}
