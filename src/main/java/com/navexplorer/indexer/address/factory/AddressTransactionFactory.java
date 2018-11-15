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

        inputs.forEach(input -> {
            if (transaction.getColdStaking() == null) {
                transaction.setColdStaking(input.isColdStaking());
            }
            if (!input.isColdStaking() || address.equals(input.getAddresses().get(1))) {
                transaction.setSent(transaction.getSent() + input.getAmount());
            }
        });

        outputs.forEach(output -> {
            if (transaction.getColdStaking() == null) {
                transaction.setColdStaking(output.isColdStaking());
            }
            if (!output.isColdStaking() || address.equals(output.getAddresses().get(1))) {
                transaction.setReceived(transaction.getReceived() + output.getAmount());
            }
        });

        if (isEmpty(transaction)) {
            return null;
        }

        String stakingAddress = blockTransaction.getOutputs().stream()
                .filter(t -> t.getAddresses().size() != 0 && !t.getAddresses().contains("Community Fund"))
                .findFirst().orElse(new Output()).getAddresses().get(0);

        if (isStaking(blockTransaction) && address.equals(stakingAddress)) {
            transaction.setType(AddressTransactionType.STAKING);
            if (transaction.getColdStaking()) {
                inputs.stream()
                        .filter(input -> input.getAddresses().size() == 2)
                        .findFirst()
                        .ifPresent(input -> transaction.setColdStakingAddress(input.getAddresses().get(0)));
            }
            return transaction;
        }

        if (isCommunityFundPayout(blockTransaction, address, stakingAddress)) {
            transaction.setType(AddressTransactionType.COMMUNITY_FUND_PAYOUT);
        } else if (isReceiving(inputs, outputs)) {
            transaction.setType(AddressTransactionType.RECEIVE);
        } else {
            transaction.setType(AddressTransactionType.SEND);
        }

        return transaction;
    }

    private Boolean isCommunityFund(String address) {
        return address.equals("Community Fund");
    }

    private Boolean isCommunityFundPayout(BlockTransaction transaction, String address, String stakingAddress) {
        return isStaking(transaction) && !address.equals(stakingAddress);
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
