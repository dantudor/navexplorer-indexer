package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.OutputType;
import org.navcoin.response.Transaction;

import java.util.Date;

public class BlockTransactionFactory {
    public static BlockTransaction createTransaction(Transaction apiTransaction) {
        BlockTransaction transaction = new BlockTransaction();
        transaction.setHash(apiTransaction.getTxid());
        transaction.setTime(new Date(apiTransaction.getTime() * 1000));
        transaction.setHeight(apiTransaction.getHeight());
        transaction.setBlockHash(apiTransaction.getBlockhash());
        transaction.setInputs(InputFactory.createInputs(apiTransaction));
        transaction.setOutputs(OutputFactory.createOutputs(apiTransaction));
        transaction.setFees(applyFees(transaction));
        transaction.setType(applyType(transaction));
        transaction.setStake(applyStaking(transaction));

        return transaction;
    }

    private static BlockTransactionType applyType(BlockTransaction transaction) {
        Double outputAmount = transaction.getOutputAmount();
        Double inputAmount = transaction.getInputAmount();

        if (transaction.hasOutputOfType(OutputType.COLD_STAKING)) {
            return BlockTransactionType.COLD_STAKING;

        }
        if (outputAmount - inputAmount > 0) {
            return BlockTransactionType.STAKING;
        }

        if (inputAmount == 0 && outputAmount == 0) {
            return BlockTransactionType.EMPTY;
        }

        return BlockTransactionType.SPEND;
    }

    private static Double applyFees(BlockTransaction transaction) {
        if (transaction.getInputAmount() - transaction.getOutputAmount() > 0) {
            return transaction.getInputAmount() - transaction.getOutputAmount();
        }

        return 0.0;
    }

    private static Double applyStaking(BlockTransaction transaction) {
        if (transaction.getOutputAmount() - transaction.getInputAmount() > 0) {
            String stakingAddress = transaction.getOutputs().stream()
                    .filter(t -> t.hasAddress() && !t.getAddress().equals("Community Fund"))
                    .findFirst().orElse(new Output()).getAddress();

            transaction.getInputs().forEach(i -> i.getAddresses().add(stakingAddress));

            return transaction.getOutputs().stream()
                    .filter(t -> t.hasAddress() && !t.getAddress().equals("Community Fund"))
                    .mapToDouble(Output::getAmount).sum() - transaction.getInputAmount();
        }

        return 0.0;
    }
}
