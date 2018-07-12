package com.navexplorer.indexer.service;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.configuration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlockStakingService {
    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    BlockRepository blockRepository;

    public void updateStakingInfo(Block block) {
        BlockTransaction transaction = blockTransactionRepository.findByBlockHashAndStakeIsGreaterThan(block.getHash(), 0.0);

        if (transaction != null) {
            block.setStake(transaction.getStake());
            Output output = getStakeOutput(transaction.getOutputs());
            if (output != null) {
                block.setStakedBy(output.getAddress());
            }

            blockRepository.save(block);
        }
    }

    private Output getStakeOutput(List<Output> outputs) {
        Optional<Output> output = outputs.stream().filter(o -> o.getAddress() != null).findFirst();

        return output.orElse(null);
    }
}
