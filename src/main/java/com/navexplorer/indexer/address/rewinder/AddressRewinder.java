package com.navexplorer.indexer.address.rewinder;

import com.navexplorer.indexer.address.transition.AddressTransition;
import com.navexplorer.library.address.repository.AddressTransactionRepository;
import com.navexplorer.library.block.entity.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressRewinder {
    private static final Logger logger = LoggerFactory.getLogger(AddressRewinder.class);

    @Autowired
    private AddressTransactionRepository addressTransactionRepository;

    @Autowired
    AddressTransition addressTransition;

    public void rewind(Block block) {
        logger.info(String.format("Rewinding addresses for block: %s", block.getHeight()));

        addressTransactionRepository.findByHeight(block.getHeight())
                .forEach(transaction -> addressTransition.down(transaction));
    }
}
