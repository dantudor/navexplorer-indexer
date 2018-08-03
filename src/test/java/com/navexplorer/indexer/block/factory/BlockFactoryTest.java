package com.navexplorer.indexer.block.factory;

import com.navexplorer.indexer.block.exception.CreateBlockException;
import com.navexplorer.library.block.entity.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

public class BlockFactoryTest {
    @Test
    public void it_can_create_a_block() {
        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();
        apiBlock.setHash("BLOCK HASH");
        apiBlock.setMerkleroot("MERKLE ROOT");
        apiBlock.setBits("BITS");
        apiBlock.setSize(700L);
        apiBlock.setVersion(10001L);
        apiBlock.setVersionHex("VERSION HEX");
        apiBlock.setNonce(99999L);
        apiBlock.setHeight(100L);
        apiBlock.setDifficulty(914.001);
        apiBlock.setConfirmations(23411L);
        apiBlock.setTime(123123123L);
        apiBlock.setTx(Arrays.asList("tx_1", "tx_2", "tx_3"));

        Block block = new BlockFactory().createBlock(apiBlock);

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
        assertThat(block.getCreated()).isEqualTo(new Date(apiBlock.getTime() * 1000L));
        assertThat(block.getTransactions()).isEqualTo(apiBlock.getTx().size());
    }

    @Test(expected = CreateBlockException.class)
    public void it_throws_a_create_block_exception_when_it_cannot_create_the_block() {
        org.navcoin.response.Block apiBlock = new org.navcoin.response.Block();

        new BlockFactory().createBlock(apiBlock);
    }
}
