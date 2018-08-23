package com.navexplorer.indexer.block.service;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.RedeemedIn;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.configuration.service.ConfigurationService;
import com.navexplorer.library.block.service.BlockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreviousInputService {
    @Autowired
    ConfigurationService configurationService;

    @Autowired
    BlockTransactionService blockTransactionService;

    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    public void updateTransaction(BlockTransaction transaction) {
        transaction.getInputs().stream().filter(i -> i.getPreviousOutput() != null).forEach(input -> {
            BlockTransaction previousTransaction = blockTransactionService.getOneByHash(input.getPreviousOutput());

            previousTransaction.getOutput(input.getIndex()).setRedeemedIn(
                    new RedeemedIn(transaction.getHash(), transaction.getHeight())
            );

            blockTransactionRepository.save(previousTransaction);
        });
    }
}
