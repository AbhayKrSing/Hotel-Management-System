package com.airbnb.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulated Stripe payment processor.
 * In production, replace method bodies with actual Stripe SDK calls.
 * Test card tokens: tok_visa, tok_mastercard → success
 * tok_chargeDeclined → declined/failed
 */
@Service
public class StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    /**
     * Simulates charging a card token.
     * @return A mock transaction ID (UUID) on success, or throws RuntimeException on decline.
     */
    public String charge(String cardToken, BigDecimal amount) {
        log.info("[StripeService] Charging {} for amount {}", cardToken, amount);

        if ("tok_chargeDeclined".equalsIgnoreCase(cardToken)) {
            throw new RuntimeException("Card declined by processor");
        }
        // Mock success — return a fake transaction ID
        String txnId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("[StripeService] Charge successful. Transaction ID: {}", txnId);
        return txnId;
    }

    /**
     * Simulates refunding a previous transaction.
     * @return A mock refund transaction ID.
     */
    public String refund(String transactionId) {
        log.info("[StripeService] Refunding transaction: {}", transactionId);
        String refundId = "ref_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("[StripeService] Refund successful. Refund ID: {}", refundId);
        return refundId;
    }
}
