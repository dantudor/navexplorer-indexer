package com.navexplorer.indexer.communityfund.indexer;

import com.mongodb.DuplicateKeyException;
import com.navexplorer.indexer.communityfund.factory.PaymentRequestFactory;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.communityfund.entity.PaymentRequest;
import com.navexplorer.library.communityfund.entity.PaymentRequestState;
import com.navexplorer.library.communityfund.repository.PaymentRequestRepository;
import com.navexplorer.library.communityfund.service.CommunityFundService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestIndexer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestIndexer.class);

    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private PaymentRequestFactory paymentRequestFactory;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    public void indexPaymentRequest(BlockTransaction transaction) {
        if (transaction.getVersion() != 5) {
            return;
        }

        try {
            PaymentRequest paymentRequest = paymentRequestFactory.createPaymentRequest(
                    navcoinService.getPaymentRequest(transaction.getHash()), transaction);

            paymentRequestRepository.save(paymentRequest);

            logger.info("Community fund payment request saved: " + paymentRequest.getHash());
        } catch (DuplicateKeyException e) {
            //
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateAllPaymentRequests() {
        updatePaymentRequestsByState(PaymentRequestState.PENDING);
        updatePaymentRequestsByState(PaymentRequestState.ACCEPTED);
        updatePaymentRequestsByState(PaymentRequestState.REJECTED);
        updatePaymentRequestsByState(PaymentRequestState.EXPIRED);
    }

    private void updatePaymentRequestsByState(PaymentRequestState state) {
        paymentRequestRepository.findAllByStateOrderByIdDesc(state).forEach(paymentRequest -> {
            paymentRequestFactory.updatePaymentRequest(paymentRequest, navcoinService.getPaymentRequest(paymentRequest.getHash()));
            paymentRequestRepository.save(paymentRequest);

            logger.info(String.format("Payment Request updated: %s %s", paymentRequest.getState(), paymentRequest.getHash()));
        });
    }
}
