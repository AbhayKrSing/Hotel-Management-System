package com.airbnb.payment.service;

import com.airbnb.payment.dto.PaymentDTO;

import java.util.UUID;

public interface PaymentService {
    PaymentDTO processPayment(PaymentDTO paymentDTO, String guestEmail);
    PaymentDTO refundPayment(UUID paymentId, String userEmail);
    PaymentDTO getPaymentById(UUID paymentId);
}
