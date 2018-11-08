package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.factory.BlockProposalVoteFactory;
import com.navexplorer.library.block.entity.BlockTransactionProposalVote;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockTransactionProposalVoteIndexer {
    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @Autowired
    BlockProposalVoteFactory blockProposalVoteFactory;

    public void indexProposalVotes(BlockTransaction transaction) {
        if (!transaction.isStaking()) {
            return;
        }

        List<BlockTransactionProposalVote> blockTransactionProposalVotes = blockProposalVoteFactory.createProposalVotes(transaction);
        if (blockTransactionProposalVotes.size() != 0) {
            transaction.setProposalVotes(blockTransactionProposalVotes);
            blockTransactionRepository.save(transaction);
        }
    }
}
