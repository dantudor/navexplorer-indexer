package com.navexplorer.indexer.block.indexer;

import com.navexplorer.indexer.block.factory.BlockPaymentRequestVoteFactory;
import com.navexplorer.library.block.entity.BlockTransactionPaymentRequestVote;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockTransactionPaymentRequestVoteIndexer {
    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @Autowired
    BlockPaymentRequestVoteFactory blockPaymentRequestVoteFactory;

    public void indexPaymentRequestVotes(BlockTransaction transaction) {
        if (!transaction.getType().equals(BlockTransactionType.STAKING)) {
            return;
        }

        List<BlockTransactionPaymentRequestVote> blockTransactionPaymentRequestVotes = blockPaymentRequestVoteFactory.createPaymentRequestVotes(transaction);
        if (blockTransactionPaymentRequestVotes.size() != 0) {
            transaction.setPaymentRequestVotes(blockTransactionPaymentRequestVotes);
            blockTransactionRepository.save(transaction);
        }
    }
}
