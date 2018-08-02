package com.navexplorer.indexer.address.indexer;

import com.navexplorer.indexer.address.transition.AddressTransition;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.block.entity.*;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.indexer.address.factory.AddressTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddressIndexer {
    private static final Logger logger = LoggerFactory.getLogger(AddressIndexer.class);

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Autowired
    private AddressTransactionFactory addressTransactionFactory;

    @Autowired
    private AddressTransition addressTransition;

    public void indexBlock(Block block) {
        logger.info(String.format("Indexing addresses for block: %s", block.getHeight()));

        blockTransactionService.getByHeight(block.getHeight()).forEach(
                blockTransaction -> getAllTransactionAddresses(blockTransaction).forEach(
                        address -> {
                            AddressTransaction addressTransaction = addressTransactionFactory.create(address, blockTransaction);
                            if (addressTransaction != null) {
                                addressTransition.up(addressTransaction);
                            }
                        }
                )
        );
    }

    private Set<String> getAllTransactionAddresses(BlockTransaction blockTx) {
        Set<String> addresses = new HashSet<>();

        blockTx.getInputs().forEach(i -> i.getAddresses().stream().collect(Collectors.toCollection(() -> addresses)));
        blockTx.getOutputs().forEach(o -> o.getAddresses().stream().collect(Collectors.toCollection(() -> addresses)));

        return addresses;
    }
}
