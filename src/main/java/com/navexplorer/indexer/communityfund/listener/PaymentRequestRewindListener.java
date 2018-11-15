package com.navexplorer.indexer.communityfund.listener;

import com.navexplorer.indexer.block.event.BlockRewindEvent;
import com.navexplorer.indexer.communityfund.rewinder.PaymentRequestRewinder;
import com.navexplorer.library.block.entity.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestRewindListener implements ApplicationListener<BlockRewindEvent> {
    @Autowired
    PaymentRequestRewinder paymentRequestRewinder;

    @Override
    public void onApplicationEvent(BlockRewindEvent event) {
        Block block = event.getBlock();

        paymentRequestRewinder.rewindPaymentRequest(block);
    }
}
