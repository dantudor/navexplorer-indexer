package com.navexplorer.indexer.block.event;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrphanedBlockEventTest {
    @Test
    public void it_takes_only_a_source_object() {
        Object source = new Object();
        OrphanedBlockEvent event = new OrphanedBlockEvent(source);

        assertThat(event.getSource()).isEqualTo(source);
    }
}
