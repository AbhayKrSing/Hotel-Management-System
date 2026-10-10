package com.airbnb.payment.service.impl;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.booking.service.BookingService;
import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.dto.RazorpayOrderDTO;
import com.airbnb.payment.enums.PaymentStatus;
import com.airbnb.payment.model.Payment;
import com.airbnb.payment.repository.PaymentRepository;
import com.airbnb.payment.service.PaymentService;
import com.airbnb.payment.service.RazorpayService;
import com.airbnb.shared.exceptions.ResourceNotFoundException;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               BookingRepository bookingRepository,
                               BookingService bookingService,
                               UserRepository userRepository,
                               RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.razorpayService = razorpayService;
    }

    @Override
    @Transactional
    public RazorpayOrderDTO createRazorpayOrder(UUID bookingId, String guestEmail) {
        userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        assertBookingPayable(booking);

        String razorpayOrderId = razorpayService.createOrder(booking.getTotalPrice(), booking.getId());
        return new RazorpayOrderDTO(
                razorpayOrderId,
                booking.getId(),
                booking.getTotalPrice(),
                "INR",
                razorpayService.getKeyId()
        );
    }

    @Override
    @Transactional
    public PaymentDTO verifyAndProcessPayment(PaymentDTO paymentDTO, String guestEmail) {
        return processPayment(paymentDTO, guestEmail);
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO, String guestEmail) {
        userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + paymentDTO.getBookingId()));

        String razorpayOrderId = paymentDTO.getRazorpayOrderId();
        String razorpayPaymentId = paymentDTO.getRazorpayPaymentId();
        String razorpaySignature = paymentDTO.getRazorpaySignature();

        // Resolve stable transaction id before create so retries can be idempotent
        String txnId = null;
        if (razorpayPaymentId != null && !razorpayPaymentId.isBlank()) {
            txnId = razorpayPaymentId;
        } else if (paymentDTO.getTransactionId() != null && !paymentDTO.getTransactionId().isBlank()) {
            txnId = paymentDTO.getTransactionId();
        }

        if (txnId != null) {
            Payment existing = paymentRepository.findByTransactionId(txnId).orElse(null);
            if (existing != null) {
                if (!existing.getBooking().getId().equals(booking.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Payment already linked to a different booking");
                }
                verifySignatureIfPresent(razorpayOrderId, razorpayPaymentId, razorpaySignature);
                if (existing.getPaymentStatus() == PaymentStatus.SUCCESS
                        && booking.getBookingStatus() != BookingStatus.CONFIRMED) {
                    booking.setBookingStatus(BookingStatus.CONFIRMED);
                    bookingRepository.save(booking);
                }
                return toPaymentResult(existing, booking, razorpayOrderId, txnId, razorpaySignature);
            }
        }

        assertBookingPayable(booking);
        verifySignatureIfPresent(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (txnId == null) {
            txnId = "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(txnId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        // Confirm the held booking only — inventory was already decremented at create time
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);
        return toPaymentResult(saved, booking, razorpayOrderId, txnId, razorpaySignature);
    }

    private void verifySignatureIfPresent(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        if (razorpayOrderId != null && razorpayPaymentId != null && razorpaySignature != null) {
            boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Razorpay payment signature");
            }
        }
    }

    private PaymentDTO toPaymentResult(Payment payment, Booking booking,
                                       String razorpayOrderId, String txnId, String razorpaySignature) {
        PaymentDTO result = convertToDTO(payment, booking.getTotalPrice());
        result.setRazorpayOrderId(razorpayOrderId);
        result.setRazorpayPaymentId(txnId);
        result.setRazorpaySignature(razorpaySignature);
        return result;
    }

    /**
     * Ensures the booking can still accept payment: not confirmed/cancelled and within the hold window.
     */
    private void assertBookingPayable(Booking booking) {
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already paid and confirmed");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot pay for a cancelled booking");
        }

        if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment window expired");
        }
    }

    @Override
    @Transactional
    public PaymentDTO refundPayment(UUID paymentId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only successful payments can be refunded");
        }

        Booking booking = payment.getBooking();
        java.math.BigDecimal amount = booking.getTotalPrice();

        String refundId = razorpayService.refund(payment.getTransactionId(), amount);
        payment.setTransactionId(refundId);
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);

        // Cancel booking and restore inventory through the shared path
        bookingService.cancelAndReleaseInventory(booking.getId());

        return convertToDTO(saved, amount);
    }

    @Override
    public PaymentDTO getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return convertToDTO(payment, payment.getBooking().getTotalPrice());
    }

    private PaymentDTO convertToDTO(Payment payment, java.math.BigDecimal amount) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setRazorpayPaymentId(payment.getTransactionId());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setAmount(amount);
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}
