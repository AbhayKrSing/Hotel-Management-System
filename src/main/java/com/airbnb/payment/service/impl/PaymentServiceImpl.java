package com.airbnb.payment.service.impl;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.enums.PaymentStatus;
import com.airbnb.payment.model.Payment;
import com.airbnb.payment.repository.PaymentRepository;
import com.airbnb.payment.service.PaymentService;
import com.airbnb.payment.service.StripeService;
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
    private final StripeService stripeService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.stripeService = stripeService;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO, String guestEmail) {
        userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + paymentDTO.getBookingId()));
//
//        if (!booking.getUser().getEmail().equals(guestEmail)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only pay for your own bookings");
//        }

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already paid and confirmed");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot pay for a cancelled booking");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);

        try {
            String transactionId = stripeService.charge(paymentDTO.getCardToken(), booking.getTotalPrice());
            payment.setTransactionId(transactionId);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            // Confirm the booking
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } catch (RuntimeException e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Payment failed: " + e.getMessage());
        }

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved, booking.getTotalPrice());
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
        boolean isGuest = booking.getUserId().equals(user.getId());
        
        //TO-DO Check logic here
//        boolean isHost = booking.getHotel().getHost().getId().equals(user.getId());
//        boolean isAdmin = user.getRoles().contains(com.airbnb.user.enums.Roles.ADMIN);
//
//        if (!isGuest && !isHost && !isAdmin) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to refund this payment");
//        }

        String refundId = stripeService.refund(payment.getTransactionId());
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
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setAmount(amount);
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}
