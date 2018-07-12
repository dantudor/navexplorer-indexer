package com.navexplorer.indexer.service;

import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.entity.AddressTransactionType;
import com.navexplorer.library.address.repository.AddressRepository;
import com.navexplorer.library.address.repository.AddressTransactionRepository;
import com.navexplorer.library.address.service.AddressService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Input;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.indexer.factory.AddressTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddressIndexingService {
    private static final Logger logger = LoggerFactory.getLogger(AddressIndexingService.class);

    @Autowired
    private BlockTransactionService blockTransactionService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressTransactionRepository addressTransactionRepository;

    public boolean indexBlock(Block block) {
        logger.info(String.format("Indexing addresses for block: %s", block.getHeight()));

        blockTransactionService.getByHeight(block.getHeight()).forEach(transaction -> {
            switch (transaction.getType()) {
                case STAKING:
                    this.createStakingTransaction(transaction);
                    break;
                case SPEND:
                    this.createSpendingTransaction(transaction);
            }
        });

        return true;
    }

    private void createStakingTransaction(BlockTransaction blockTransaction) {
        getAllTransactionAddresses(blockTransaction).forEach(a -> {
            AddressTransaction addressTransaction = AddressTransactionFactory.create(a, blockTransaction);

            List<Input> inputs = blockTransaction.getInputsByAddress(a);
            List<Output> outputs = blockTransaction.getOutputsByAddress(a);

            if (blockTransaction.hasInputWithAddress(a)) {
                addressTransaction.setType(AddressTransactionType.STAKING);
                addressTransaction.addSent(inputs.stream().mapToDouble(Input::getAmount).sum());
                addressTransaction.addReceived(outputs.stream().mapToDouble(Output::getAmount).sum());
            } else if (a.equals("Community Fund")) {
                addressTransaction.setType(AddressTransactionType.COMMUNITY_FUND);
                addressTransaction.addReceived(outputs.stream().mapToDouble(Output::getAmount).sum());
            }

            saveAddressTransaction(addressTransaction);
        });
    }

    private void createSpendingTransaction(BlockTransaction blockTransaction) {
        getAllTransactionAddresses(blockTransaction).forEach(a -> {
            AddressTransaction addressTransaction = AddressTransactionFactory.create(a, blockTransaction);

            List<Input> inputs = blockTransaction.getInputsByAddress(a);
            List<Output> outputs = blockTransaction.getOutputsByAddress(a);

            addressTransaction.addSent(inputs.stream().mapToDouble(Input::getAmount).sum());
            addressTransaction.addReceived(outputs.stream().mapToDouble(Output::getAmount).sum());

            if (addressTransaction.getSent() < addressTransaction.getReceived()) {
                addressTransaction.setType(AddressTransactionType.RECEIVE);
            } else {
                addressTransaction.setType(AddressTransactionType.SEND);
            }

            saveAddressTransaction(addressTransaction);
        });
    }

    private Set<String> getAllTransactionAddresses(BlockTransaction transaction) {
        Set<String> addresses = new HashSet<>();
        transaction.getInputs().forEach(i -> addresses.add(i.getAddress()));

        transaction.getOutputs().forEach(o ->
                o.getAddresses().stream().collect(Collectors.toCollection(() -> addresses))
        );

        return addresses;
    }

    private void saveAddressTransaction(AddressTransaction addressTransaction) {
        Address address = addressService.getAddress(addressTransaction.getAddress());
        address.setBlockIndex(addressTransaction.getHeight());

        switch (addressTransaction.getType()) {
            case STAKING:
                address.addStaked(addressTransaction.getSent(), addressTransaction.getReceived());
                break;
            case SEND:
            case RECEIVE:
                address.addSpend(addressTransaction.getSent(), addressTransaction.getReceived());
                break;
            case COMMUNITY_FUND:
                address.addSpend(addressTransaction.getSent(), addressTransaction.getReceived());
                break;
        }

        addressTransaction.setBalance(address.getBalance());
        addressTransactionRepository.save(addressTransaction);
        addressRepository.save(address);
    }
}
