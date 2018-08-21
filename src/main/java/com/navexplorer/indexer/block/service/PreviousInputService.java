package com.navexplorer.indexer.block.service;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.RedeemedIn;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.configuration.service.ConfigurationService;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.indexer.block.exception.PreviousTransactionMissingException;
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
            if (previousTransaction == null) {
                throw new PreviousTransactionMissingException("Could not find previous transaction", input.getPreviousOutput());
            }

            if (input.getIndex() == null) {
                throw new RuntimeException(String.format("Input index is null. Tx: %s Addr: %s", transaction.getHash(), input.getAddresses().toString()));
            }

            Output previousOutput = previousTransaction.getOutput(input.getIndex());
            if (previousOutput == null) {
                throw new RuntimeException(String.format("Previous output is null. Tx: %s Addr: %s", previousTransaction.getHash(), input.getAddresses().toString()));
            }

            previousOutput.setRedeemedIn(new RedeemedIn(transaction.getHash(), transaction.getHeight()));

            blockTransactionRepository.save(previousTransaction);

            input.setPreviousOutputBlock(previousTransaction.getHeight());
            input.setAddresses(previousOutput.getAddresses());
            input.setAmount(previousOutput.getAmount());

            blockTransactionRepository.save(transaction);
        });
    }
}
