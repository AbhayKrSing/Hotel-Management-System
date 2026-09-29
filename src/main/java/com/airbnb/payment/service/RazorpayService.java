package com.airbnb.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RazorpayService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayService.class);

    @Value("${razorpay.key.id:rzp_test_mockKeyId}")
    private String keyId;

    @Value("${razorpay.key.secret:mockSecretKey}")
    private String keySecret;

    public String getKeyId() {
        return keyId;
    }

    /**
     * Creates a Razorpay Order.
     * @param amount Amount in main currency unit (e.g. INR)
     * @param bookingId Booking reference ID
     * @return Created Razorpay Order ID (e.g. order_EKz84351025)
     */
    public String createOrder(BigDecimal amount, UUID bookingId) {
        log.info("[RazorpayService] Creating order for bookingId: {} with amount: {}", bookingId, amount);
        try {
            if (isMockMode()) {
                String mockOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
                log.info("[RazorpayService] Mock mode active. Generated Order ID: {}", mockOrderId);
                return mockOrderId;
            }

            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in smallest currency sub-unit (e.g., paise: amount * 100)
            long amountInPaise = amount.multiply(new BigDecimal("100")).longValue();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + bookingId.toString().substring(0, 8));

            Order order = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = order.get("id");
            log.info("[RazorpayService] Razorpay order created successfully. Order ID: {}", razorpayOrderId);
            return razorpayOrderId;
        } catch (Exception e) {
            log.error("[RazorpayService] Error creating Razorpay order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies the payment signature sent by Razorpay Checkout frontend modal.
     */
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        log.info("[RazorpayService] Verifying signature for orderId: {}, paymentId: {}", razorpayOrderId, razorpayPaymentId);
        if (isMockMode()) {
            log.info("[RazorpayService] Mock mode active. Signature verification bypassed.");
            return true;
        }
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            return Utils.verifyPaymentSignature(attributes, keySecret);
        } catch (Exception e) {
            log.error("[RazorpayService] Signature verification failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Initiates a refund for a payment.
     */
    public String refund(String paymentId, BigDecimal amount) {
        log.info("[RazorpayService] Refunding paymentId: {}", paymentId);
        try {
            if (isMockMode()) {
                String mockRefundId = "ref_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
                log.info("[RazorpayService] Mock mode active. Generated Refund ID: {}", mockRefundId);
                return mockRefundId;
            }

            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject refundRequest = new JSONObject();
            if (amount != null) {
                long amountInPaise = amount.multiply(new BigDecimal("100")).longValue();
                refundRequest.put("amount", amountInPaise);
            }
            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
            String refundId = refund.get("id");
            log.info("[RazorpayService] Razorpay refund successful. Refund ID: {}", refundId);
            return refundId;
        } catch (Exception e) {
            log.error("[RazorpayService] Error processing refund: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refund Razorpay payment: " + e.getMessage(), e);
        }
    }

    private boolean isMockMode() {
        return keyId == null || keyId.startsWith("rzp_test_mock") || keySecret == null || keySecret.startsWith("mockSecret");
    }
}
