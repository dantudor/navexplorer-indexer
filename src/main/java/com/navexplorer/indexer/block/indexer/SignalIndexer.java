package com.navexplorer.indexer.block.indexer;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockSignal;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.softfork.entity.SoftFork;
import com.navexplorer.library.softfork.entity.SoftForkState;
import com.navexplorer.library.softfork.repository.SoftForkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalIndexer {
    @Autowired
    private SoftForkRepository softForkRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Value("${navcoin.blocksInCycle}")
    private Integer blocksInCycle;

    public void indexBlock(Block block) {
        List<SoftFork> softForks = softForkRepository.findAll();

        softForks.forEach(softFork -> {
            boolean signalling = (block.getVersion() >> softFork.getSignalBit() & 1) == 1;

            block.getSignals().add(new BlockSignal(softFork.getName(), signalling));
        });

        block.setBlockCycle(
                ((Double) Math.ceil(block.getHeight().intValue() / blocksInCycle)).intValue() + 1
        );
        blockRepository.save(block);

        updateSoftForks(block);
    }

    private void updateSoftForks(Block block) {
        softForkRepository.findAllByStateIsIn(SoftForkState.getNonTerminalStates()).forEach(softFork -> {
            Integer cycleEnd = (blocksInCycle * block.getBlockCycle()) - 1;
            Integer cycleStart = cycleEnd - blocksInCycle + 1;
            Integer blockIndexInCycle = block.getHeight().intValue() - cycleStart;

            switch (softFork.getState()) {
                case DEFINED:
                    softFork.setState(SoftForkState.STARTED);
                    softFork.setBlocksSignalling(0);
                case STARTED:
                    if (blockIndexInCycle == 0) {
                        softFork.startNewCycle();
                    }

                    if (block.getSignals().stream().anyMatch(s -> s.getName().equals(softFork.getName()) && s.isSignalling())) {
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
