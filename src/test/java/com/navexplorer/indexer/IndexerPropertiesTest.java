package com.navexplorer.indexer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class IndexerPropertiesTest {
    @Test
    public void it_can_set_and_get_the_zeromq_address() {
        String address = "ADDRESS";

        IndexerProperties indexerProperties = new IndexerProperties();
        indexerProperties.setAddress(address);

        assertThat(indexerProperties.getAddress()).isEqualTo(address);
    }
}
