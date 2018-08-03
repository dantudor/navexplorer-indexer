package com.navexplorer.indexer.block.event;

import com.navexplorer.library.block.entity.BlockTransaction;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockTransactionIndexedEventTest {
    @Test
    public void it_takes_a_block_transaction() {
        BlockTransaction transaction = new BlockTransaction();
        BlockTransactionIndexedEvent event = new BlockTransactionIndexedEvent(new Object(), transaction);

        assertThat(event.getTransaction()).isEqualTo(transaction);
    }
}
