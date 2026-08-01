package com.airbnb.payment.controller;

import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments
     * Guest pays for a booking using a card token.
     * Body: { "bookingId": "...", "cardToken": "tok_visa" }
     */
    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentDTO paymentDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        PaymentDTO result = paymentService.processPayment(paymentDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * POST /api/payments/{id}/refund
     * Guest, host, or admin can trigger a refund.
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        PaymentDTO result = paymentService.refundPayment(id, auth.getName());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/payments/{id}
     * Retrieve payment details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
