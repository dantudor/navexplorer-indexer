package com.navexplorer.indexer.block.factory;

import com.navexplorer.indexer.block.exception.CreateBlockException;
import com.navexplorer.library.block.entity.Block;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class BlockFactory {
    private static final Logger logger = LoggerFactory.getLogger(BlockFactory.class);

    @Value("${navcoin.blocksInCycle")
    private static Integer blocksInCycle;

    public static Block createBlock(org.navcoin.response.Block apiBlock) {
        logger.info(String.format("Creating new block at height %s", apiBlock.getHeight()));

        try {
            return new Block(
                    apiBlock.getHash(),
                    apiBlock.getMerkleroot(),
                    apiBlock.getBits(),
                    apiBlock.getSize(),
                    apiBlock.getVersion(),
                    apiBlock.getVersionHex(),
                    apiBlock.getNonce(),
                    apiBlock.getHeight(),
                    apiBlock.getDifficulty(),
                    apiBlock.getConfirmations(),
                    new Date(apiBlock.getTime() * 1000L),
                    apiBlock.getTx().size()
            );
        } catch (RuntimeException e) {
            throw new CreateBlockException(e);
        }
    }
}
