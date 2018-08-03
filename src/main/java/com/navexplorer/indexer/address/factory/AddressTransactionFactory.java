package com.navexplorer.indexer.address.factory;

import com.navexplorer.library.address.entity.*;
import com.navexplorer.library.block.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressTransactionFactory {
    public AddressTransaction create(String address, BlockTransaction blockTransaction) {
        if (blockTransaction.getType().equals(BlockTransactionType.EMPTY)) {
            return null;
        }

        List<Input> inputs = blockTransaction.getInputsByAddress(address);
        List<Output> outputs = blockTransaction.getOutputsByAddress(address);

        AddressTransaction transaction = new AddressTransaction();
        transaction.setAddress(address);
        transaction.setTransaction(blockTransaction.getHash());
        transaction.setHeight(blockTransaction.getHeight());
        transaction.setTime(blockTransaction.getTime());

        if (isCommunityFund(address)) {
            transaction.setType(AddressTransactionType.COMMUNITY_FUND);
            transaction.setReceived(outputs.stream().mapToDouble(Output::getAmount).sum());

            return transaction;
        }

        inputs.forEach(input -> transaction.setSent(transaction.getSent() + input.getAmount()));
        outputs.forEach(output -> transaction.setReceived(transaction.getReceived() + output.getAmount()));

        if (isEmpty(transaction)) {
            return null;
        }

        if (isStaking(blockTransaction)) {
            transaction.setType(AddressTransactionType.STAKING);

            return transaction;
        }

        if (isReceiving(inputs, outputs)) {
            transaction.setType(AddressTransactionType.RECEIVE);
        } else {
            transaction.setType(AddressTransactionType.SEND);
        }

        return transaction;
    }

    private Boolean isCommunityFund(String address) {
        return address.equals("Community Fund");
    }

    private Boolean isEmpty(AddressTransaction transaction) {
        return transaction.getSent() == 0.0 && transaction.getReceived() == 0.0;
    }

    private Boolean isStaking(BlockTransaction transaction) {
        return transaction.getType().equals(BlockTransactionType.STAKING);
    }

    private Boolean isReceiving(List<Input> inputs, List<Output> outputs) {
        Double inputAmount = inputs.stream().mapToDouble(Input::getAmount).sum();
        Double outputAmount = outputs.stream().mapToDouble(Output::getAmount).sum();

        return inputAmount < outputAmount;
    }
}
