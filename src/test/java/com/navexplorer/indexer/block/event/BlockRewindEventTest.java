package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.Block;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockRewindEventTest {
    @Test
    public void it_takes_a_block() {
        Block block = new Block();
        BlockRewindEvent event = new BlockRewindEvent(new Object(), block);

        assertThat(event.getBlock()).isEqualTo(block);
    }
}
