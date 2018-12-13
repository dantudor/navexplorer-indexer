package com.navexplorer.indexer.address.factory;

import com.navexplorer.library.address.entity.*;
import com.navexplorer.library.block.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressTransactionFactory {
    public AddressTransaction create(String address, BlockTransaction blockTransaction) {
        if (blockTransaction.isEmpty()) {
            return null;
        }

        List<Input> inputs = blockTransaction.getInputsByAddress(address);
        List<Output> outputs = blockTransaction.getOutputsByAddress(address);
        Double inputAmount = inputs.stream().mapToDouble(Input::getAmount).sum();
        Double outputAmount = outputs.stream().mapToDouble(Output::getAmount).sum();

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(address);
        transaction.setTransaction(blockTransaction.getHash());
        transaction.setHeight(blockTransaction.getHeight());
        transaction.setTime(blockTransaction.getTime());

        if (hasColdStakingInputForAddress(inputs, address) || hasColdStakingOutputForAddress(outputs, address)) {
            if (blockTransaction.isSpend()) {
                if (inputAmount > outputAmount) {
                    transaction.setType(AddressTransactionType.SEND);
                } else {
                    transaction.setType(AddressTransactionType.RECEIVE);
                }
            } else if (blockTransaction.isColdStaking()) {
                transaction.setType(AddressTransactionType.COLD_STAKING);
            }

            inputs.forEach(input -> {
                if (input.getPreviousOutputType().equals(OutputType.COLD_STAKING) && input.getAddresses().get(0).equals(address)) {
                    transaction.setColdStaking(true);
                    transaction.setColdStakingSent(transaction.getColdStakingSent() + input.getAmount());
                } else {
                    transaction.setStandard(true);
                    transaction.setSent(transaction.getSent() + input.getAmount());
                }
            });

            outputs.forEach(output -> {
                if (output.isColdStaking() && output.getAddresses().get(0).equals(address)) {
                    transaction.setColdStaking(true);
                    transaction.setColdStakingReceived(transaction.getColdStakingReceived() + output.getAmount());
                } else {
                    transaction.setStandard(true);
                    transaction.setReceived(transaction.getReceived() + output.getAmount());
                }
            });

            return transaction;
        }

        inputs.forEach(input -> transaction.setSent(transaction.getSent() + input.getAmount()));
        outputs.forEach(output -> transaction.setReceived(transaction.getReceived() + output.getAmount()));
        transaction.setStandard(true);

        if (blockTransaction.isStaking()) {
            transaction.setType(AddressTransactionType.STAKING);
            return transaction;
        }

        transaction.setType(inputAmount < outputAmount ? AddressTransactionType.RECEIVE : AddressTransactionType.SEND);
        return transaction;
    }

    private boolean hasColdStakingOutputForAddress(List<Output> outputs, String address) {
        return outputs.stream().anyMatch(o -> o.hasAddress(address) && o.getType().equals(OutputType.COLD_STAKING));
    }

    private boolean hasColdStakingInputForAddress(List<Input> inputs, String address) {
        return inputs.stream().anyMatch(i -> i.hasAddress(address) && i.getPreviousOutputType() == OutputType.COLD_STAKING);
    }
}
