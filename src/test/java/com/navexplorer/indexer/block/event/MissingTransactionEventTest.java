package com.navexplorer.indexer.block.event;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MissingTransactionEventTest {
    @Test
    public void it_takes_a_hash() {
        String hash = "HASH";
        MissingTransactionEvent event = new MissingTransactionEvent(new Object(), hash);

        assertThat(event.getHash()).isEqualTo(hash);
    }
}
