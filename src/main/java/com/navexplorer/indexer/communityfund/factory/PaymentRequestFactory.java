package com.navexplorer.indexer.communityfund.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.communityfund.entity.*;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestFactory {
    public PaymentRequest createPaymentRequest(org.navcoin.response.PaymentRequest apiPaymentRequest, BlockTransaction transaction) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCreatedAt(transaction.getTime());
        paymentRequest.setProposalHash((String) transaction.getAnonDestinationObject().get("h"));

        return updatePaymentRequest(paymentRequest, apiPaymentRequest);
    }

    public PaymentRequest updatePaymentRequest(PaymentRequest paymentRequest, org.navcoin.response.PaymentRequest apiPaymentRequest) {
        paymentRequest.setVersion(apiPaymentRequest.getVersion());
        paymentRequest.setHash(apiPaymentRequest.getHash());
        paymentRequest.setBlockHash(apiPaymentRequest.getBlockHash());
        paymentRequest.setDescription(apiPaymentRequest.getDescription());
        paymentRequest.setRequestedAmount(apiPaymentRequest.getRequestedAmount());
        paymentRequest.setState(PaymentRequestState.fromId(apiPaymentRequest.getState()));
        paymentRequest.setStateChangedOnBlock(apiPaymentRequest.getStateChangedOnBlock());
        paymentRequest.setStatus(apiPaymentRequest.getStatus());
        paymentRequest.setVotesYes(apiPaymentRequest.getVotesYes());
        paymentRequest.setVotesNo(apiPaymentRequest.getVotesNo());
        paymentRequest.setVotingCycle(apiPaymentRequest.getVotingCycle());
        paymentRequest.setPaidOnBlock(apiPaymentRequest.getPaidOnBlock());

        return paymentRequest;
    }
}
