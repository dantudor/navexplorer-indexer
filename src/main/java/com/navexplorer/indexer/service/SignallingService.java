package com.navexplorer.indexer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockSignal;
import com.navexplorer.library.softfork.entity.SoftFork;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.softfork.entity.SoftForkState;
import com.navexplorer.library.softfork.repository.SoftForkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SignallingService {
    private static final Logger logger = LoggerFactory.getLogger(SignallingService.class);

    @Autowired
    private SoftForkRepository softForkRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Value("${navcoin.blocksInCycle}")
    private Integer blocksInCycle;

    public void importSignals() {
        try {
            File signalsFile = new File("/data/softForks.json");

            ObjectMapper mapper = new ObjectMapper();
            List<SoftFork> softForks = mapper.readValue(signalsFile, new TypeReference<List<SoftFork>>(){});

            softForks.forEach(s -> {
                s.setState(SoftForkState.DEFINED);
                try {
                    softForkRepository.save(s);
                } catch (Exception e) {
                    // only save if new
                }
            });
        } catch (IOException e) {
            System.out.print(e);
            logger.error("Could not read softForks.json");
        }
    }

    public void setSignallingForBlock(Block block) {
        List<SoftFork> softForks = softForkRepository.findAll();

        softForks.forEach(softFork -> {
            boolean signalling = (block.getVersion() >> softFork.getSignalBit() & 1) == 1;

            block.getSignals().add(new BlockSignal(softFork.getName(), signalling));
        });

        block.setBlockCycle(
                ((Double) Math.ceil(block.getHeight().intValue() / blocksInCycle)).intValue() + 1
        );
    }

    public void updateSoftForks(Block block) {
        softForkRepository.findAllByStateIsIn(SoftForkState.getNonTerminalStates()).forEach(softFork -> {
            Integer cycleEnd = (blocksInCycle * block.getBlockCycle()) - 1;
            Integer cycleStart = cycleEnd - blocksInCycle + 1;
            Integer blockIndexInCycle = block.getHeight().intValue() - cycleStart;

            if (softFork.getSignalledToBlock() != null && softFork.getSignalledToBlock() >= block.getHeight()) {
                // Don't reapply blocks that have been rewound
                return;
            }

            switch (softFork.getState()) {
                case DEFINED:
                    softFork.setState(SoftForkState.STARTED);
                    softFork.setBlocksSignalling(0);
                case STARTED:
                    if (blockIndexInCycle == 0) {
                        softFork.setBlocksSignalling(0);
                    }
                    if (block.getSignals().stream().anyMatch(s -> s.getName().equals(softFork.getName()) && s.isSignalling())) {
                        // latest block was signalling the soft fork
                        softFork.incrementSignalling();
                        softFork.setSignalledToBlock(block.getHeight());
                    }

                    if (softFork.getBlocksSignalling() >= (blocksInCycle * 0.75)) {
                        softFork.setLockedInHeight(new Long(cycleEnd) + 1);
                        softFork.setActivationHeight(new Long(cycleEnd) + 1 + blocksInCycle);

                        if (blockIndexInCycle.equals(cycleEnd - cycleStart)) {
                            softFork.setState(SoftForkState.LOCKED_IN);
                        }
                    }
                    softForkRepository.save(softFork);
                    break;

                case LOCKED_IN:
                    if (blockIndexInCycle.equals(cycleEnd - cycleStart)) {
                        logger.info(softFork.getName() + " Soft Fork is ACTIVE");
                        softFork.setState(SoftForkState.ACTIVE);
                        softForkRepository.save(softFork);
                    }
                    break;

                case ACTIVE:
                    // Terminal
                    break;

                case FAILED:
                    // Terminal
                    break;
            }
        });
    }
}
