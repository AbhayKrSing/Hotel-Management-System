package com.airbnb.payment.service;

import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.dto.RazorpayOrderDTO;

import java.util.UUID;

public interface PaymentService {
    RazorpayOrderDTO createRazorpayOrder(UUID bookingId, String guestEmail);
    PaymentDTO verifyAndProcessPayment(PaymentDTO paymentDTO, String guestEmail);
    PaymentDTO processPayment(PaymentDTO paymentDTO, String guestEmail);
    PaymentDTO refundPayment(UUID paymentId, String userEmail);
    PaymentDTO getPaymentById(UUID paymentId);
}
