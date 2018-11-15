package com.navexplorer.indexer.communityfund.rewinder;

import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.communityfund.repository.PaymentRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestRewinder {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestRewinder.class);

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    public void rewindPaymentRequest(Block block) {
        paymentRequestRepository.findByBlockHash(block.getHash()).forEach(paymentRequest -> {
            paymentRequestRepository.delete(paymentRequest);
            logger.info("Community fund - Payment request deleted: " + paymentRequest.getHash());
        });
    }
}
