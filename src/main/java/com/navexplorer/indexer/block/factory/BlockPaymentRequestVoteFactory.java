package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionPaymentRequestVote;
import com.navexplorer.library.block.entity.OutputType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockPaymentRequestVoteFactory {
    public List<BlockTransactionPaymentRequestVote> createPaymentRequestVotes(BlockTransaction transaction) {
        List<BlockTransactionPaymentRequestVote> paymentRequestVotes = new ArrayList<>();

        transaction.getOutputs().forEach(output -> {
            if (output.getType().equals(OutputType.PAYMENT_REQUEST_YES_VOTE)) {
                BlockTransactionPaymentRequestVote vote = new BlockTransactionPaymentRequestVote();
                vote.setHash(output.getHash());
                vote.setVote(true);
                paymentRequestVotes.add(vote);
            } else if (output.getType().equals(OutputType.PAYMENT_REQUEST_NO_VOTE)) {
                BlockTransactionPaymentRequestVote vote = new BlockTransactionPaymentRequestVote();
                vote.setHash(output.getHash());
                vote.setVote(false);
                paymentRequestVotes.add(vote);
            }
        });

        return paymentRequestVotes;
    }
}
