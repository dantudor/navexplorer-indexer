package com.navexplorer.indexer.service;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.RedeemedIn;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.configuration.service.ConfigurationService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.indexer.exception.PreviousTransactionMissingException;
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
        transaction.getInputs().forEach(input -> {
            if (input.getPreviousOutput() != null) {
                BlockTransaction previousTransaction = blockTransactionService.getOneByHash(input.getPreviousOutput());
                if (previousTransaction == null) {
                    throw new PreviousTransactionMissingException(
                            String.format("Could not find previous transaction: %s", input.getPreviousOutput()),
                            input.getPreviousOutput()
                    );
                }

                if (input.getIndex() == null) {
                    throw new RuntimeException(String.format("Input index is null. Tx: %s Addr: %s", transaction.getHash(), input.getAddress()));
                }

                Output previousOutput = previousTransaction.getOutput(input.getIndex());
                if (previousOutput == null) {
                    throw new RuntimeException(String.format("Previous output is null. Tx: %s Addr: %s", previousTransaction.getHash(), input.getAddress()));
                }

                previousOutput.setRedeemedIn(new RedeemedIn(transaction.getHash(), transaction.getHeight()));

                blockTransactionRepository.save(previousTransaction);

                input.setPreviousOutputBlock(previousTransaction.getHeight());
                input.setAddress(previousOutput.getAddress());
            }
        });
    }
}
