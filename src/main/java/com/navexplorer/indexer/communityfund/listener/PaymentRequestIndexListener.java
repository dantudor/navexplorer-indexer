package com.navexplorer.indexer.communityfund.listener;

import com.navexplorer.indexer.block.event.BlockTransactionIndexedEvent;
import com.navexplorer.indexer.communityfund.indexer.PaymentRequestIndexer;
import com.navexplorer.library.block.entity.BlockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestIndexListener implements ApplicationListener<BlockTransactionIndexedEvent> {
    @Autowired
    PaymentRequestIndexer paymentRequestIndexer;

    @Override
    public void onApplicationEvent(BlockTransactionIndexedEvent event) {
        BlockTransaction transaction = event.getTransaction();
        if (transaction.getVersion() != 5) {
            return;
        }

        paymentRequestIndexer.indexPaymentRequest(transaction);
    }
}
