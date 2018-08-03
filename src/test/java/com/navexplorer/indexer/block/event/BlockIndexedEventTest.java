package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.Block;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockIndexedEventTest {
    @Test
    public void it_takes_a_block() {
        Block block = new Block();
        BlockIndexedEvent event = new BlockIndexedEvent(new Object(), block);

        assertThat(event.getBlock()).isEqualTo(block);
    }
}
