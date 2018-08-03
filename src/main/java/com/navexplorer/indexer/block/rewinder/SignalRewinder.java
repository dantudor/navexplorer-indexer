package com.navexplorer.indexer.block.rewinder;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.softfork.entity.SoftForkState;
import com.navexplorer.library.softfork.repository.SoftForkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignalRewinder {
    @Autowired
    private SoftForkRepository softForkRepository;

    @Value("${navcoin.blocksInCycle}")
    private Integer blocksInCycle;

    public void rewindBlock(Block block) {
        softForkRepository.findAllBySignalledToBlock(block.getHeight()).forEach(softFork -> {
            softFork.setSignalledToBlock(block.getHeight() - 1);

            if (block.getSignals().stream().anyMatch(s -> s.getName().equals(softFork.getName()) && s.isSignalling())) {
                softFork.setBlocksSignalling(softFork.getBlocksSignalling() - 1);
            }

            switch (softFork.getState()) {
                case LOCKED_IN:
                    softFork.setState(SoftForkState.STARTED);
                    break;
                case STARTED:
                    if (softFork.getBlocksSignalling() < (blocksInCycle * 0.75)) {
                        softFork.setLockedInHeight(null);
                        softFork.setActivationHeight(null);
                    }
            }

            softForkRepository.save(softFork);
        });
    }
}
