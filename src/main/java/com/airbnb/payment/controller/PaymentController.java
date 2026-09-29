package com.airbnb.payment.controller;

import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.dto.RazorpayOrderDTO;
import com.airbnb.payment.service.PaymentService;
import com.airbnb.shared.dto.ApiResponse;
import com.airbnb.shared.exceptions.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments/create-order/{bookingId}
     * Guest initiates payment for a booking by path variable. Creates a Razorpay Order ID.
     */
    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<ApiResponse<RazorpayOrderDTO>> createRazorpayOrder(@PathVariable UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        RazorpayOrderDTO result = paymentService.createRazorpayOrder(bookingId, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Razorpay order created successfully", result));
    }

    /**
     * POST /api/payments/create-order
     * Guest initiates payment by sending JSON body: { "bookingId": "..." }
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<RazorpayOrderDTO>> createRazorpayOrderFromBody(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        String bookingIdStr = request.get("bookingId");
        if (bookingIdStr == null) {
            throw new IllegalArgumentException("bookingId is required");
        }
        UUID bookingId = UUID.fromString(bookingIdStr);
        RazorpayOrderDTO result = paymentService.createRazorpayOrder(bookingId, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Razorpay order created successfully", result));
    }

    /**
     * POST /api/payments/verify
     * Verifies Razorpay payment signature and confirms booking.
     * Body: { "bookingId": "...", "razorpayOrderId": "...", "razorpayPaymentId": "...", "razorpaySignature": "..." }
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentDTO>> verifyPayment(@RequestBody PaymentDTO paymentDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        PaymentDTO result = paymentService.verifyAndProcessPayment(paymentDTO, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment verified and processed successfully", result));
    }

    /**
     * POST /api/payments
     * Guest pays for a booking / submits Razorpay payment details.
     * Body: { "bookingId": "...", "razorpayOrderId": "...", "razorpayPaymentId": "...", "razorpaySignature": "..." }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@RequestBody PaymentDTO paymentDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        PaymentDTO result = paymentService.processPayment(paymentDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Payment processed successfully", result));
    }

    /**
     * POST /api/payments/{id}/refund
     * Guest, host, or admin can trigger a refund.
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentDTO>> refundPayment(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        PaymentDTO result = paymentService.refundPayment(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment refunded successfully", result));
    }

    /**
     * GET /api/payments/{id}
     * Retrieve payment details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPayment(@PathVariable UUID id) {
        PaymentDTO result = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment fetched successfully", result));
    }
}
