package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.OutputType;
import org.navcoin.response.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class BlockTransactionFactory {
    @Autowired
    InputFactory inputFactory;

    @Autowired
    OutputFactory outputFactory;

    public BlockTransaction createTransaction(Transaction apiTransaction) {
        BlockTransaction transaction = new BlockTransaction();
        transaction.setHash(apiTransaction.getTxid());
        transaction.setTime(new Date(apiTransaction.getTime() * 1000));
        transaction.setHeight(apiTransaction.getHeight());
        transaction.setBlockHash(apiTransaction.getBlockhash());
        transaction.setInputs(inputFactory.createInputs(apiTransaction));
        transaction.setOutputs(outputFactory.createOutputs(apiTransaction));
        transaction.setFees(applyFees(transaction));
        transaction.setType(applyType(transaction));
        transaction.setStake(applyStaking(transaction));
        transaction.setVersion(apiTransaction.getVersion());
        transaction.setAnonDestination(apiTransaction.getAnonDestination());

        return transaction;
    }

    private BlockTransactionType applyType(BlockTransaction transaction) {
        Double outputAmount = transaction.getOutputAmount();
        Double inputAmount = transaction.getInputAmount();

        if (outputAmount - inputAmount > 0) {
            if (transaction.hasOutputOfType(OutputType.COLD_STAKING)) {
                return BlockTransactionType.COLD_STAKING;
            } else {
                return BlockTransactionType.STAKING;
            }
        }

        if (inputAmount == 0 && outputAmount == 0) {
            return BlockTransactionType.EMPTY;
        }

        return BlockTransactionType.SPEND;
    }

    private Double applyFees(BlockTransaction transaction) {
        if (transaction.getInputAmount() - transaction.getOutputAmount() > 0) {
            return transaction.getInputAmount() - transaction.getOutputAmount();
        }

        return 0.0;
    }

    private Double applyStaking(BlockTransaction transaction) {
        if (transaction.getOutputAmount() - transaction.getInputAmount() > 0) {
            String stakingAddress = transaction.getOutputs().stream()
                    .filter(t -> t.getAddresses().size() != 0 && !t.getAddresses().contains("Community Fund"))
                    .findFirst().orElse(new Output()).getAddresses().get(0);

            if (!transaction.hasInputWithAddress(stakingAddress)) {
                transaction.getInputs().forEach(i -> i.getAddresses().add(stakingAddress));
            }

            return transaction.getOutputs().stream()
                    .filter(t -> t.getAddresses().contains(stakingAddress))
                    .mapToDouble(Output::getAmount).sum() - transaction.getInputAmount();
        }

        return 0.0;
    }
}
