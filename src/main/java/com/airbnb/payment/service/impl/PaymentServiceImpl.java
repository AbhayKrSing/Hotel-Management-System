package com.airbnb.payment.service.impl;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
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
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               BookingRepository bookingRepository,
                               UserRepository userRepository,
                               RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
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

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already paid and confirmed");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot pay for a cancelled booking");
        }

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

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already paid and confirmed");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot pay for a cancelled booking");
        }

        String razorpayOrderId = paymentDTO.getRazorpayOrderId();
        String razorpayPaymentId = paymentDTO.getRazorpayPaymentId();
        String razorpaySignature = paymentDTO.getRazorpaySignature();

        // If signature verification info is provided, verify signature
        if (razorpayOrderId != null && razorpayPaymentId != null && razorpaySignature != null) {
            boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Razorpay payment signature");
            }
        }

        // Determine transaction ID (use paymentId if available, or generate/reuse transaction ID)
        String txnId = (razorpayPaymentId != null && !razorpayPaymentId.isBlank()) 
                ? razorpayPaymentId 
                : (paymentDTO.getTransactionId() != null ? paymentDTO.getTransactionId() : "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(txnId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        // Confirm the booking
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);

        PaymentDTO result = convertToDTO(saved, booking.getTotalPrice());
        result.setRazorpayOrderId(razorpayOrderId);
        result.setRazorpayPaymentId(txnId);
        result.setRazorpaySignature(razorpaySignature);
        return result;
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

        String refundId = razorpayService.refund(payment.getTransactionId(), booking.getTotalPrice());
        payment.setTransactionId(refundId);
        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        // Cancel the booking if not already cancelled
        if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved, booking.getTotalPrice());
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
