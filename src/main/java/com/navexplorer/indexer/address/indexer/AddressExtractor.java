package com.navexplorer.indexer.address.indexer;

import com.navexplorer.library.block.entity.BlockTransaction;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressExtractor {
    public Set<String> getAllAddressesFromBlockTransaction(BlockTransaction blockTx) {
        Set<String> addresses = new HashSet<>();

        blockTx.getInputs().forEach(i -> i.getAddresses().stream().collect(Collectors.toCollection(() -> addresses)));
        blockTx.getOutputs().forEach(o -> o.getAddresses().stream().collect(Collectors.toCollection(() -> addresses)));

        return addresses;
    }
}
