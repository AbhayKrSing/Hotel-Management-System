package com.airbnb.payment.service.impl;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.booking.service.BookingService;
import com.airbnb.payment.dto.PaymentDTO;
import com.airbnb.payment.enums.PaymentStatus;
import com.airbnb.payment.model.Payment;
import com.airbnb.payment.repository.PaymentRepository;
import com.airbnb.payment.service.RazorpayService;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RazorpayService razorpayService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User guest;
    private Booking pendingBooking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setEmail("guest@example.com");

        pendingBooking = new Booking();
        pendingBooking.setId(UUID.randomUUID());
        pendingBooking.setUserId(guest.getId());
        pendingBooking.setBookingStatus(BookingStatus.PENDING);
        pendingBooking.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        pendingBooking.setTotalPrice(new BigDecimal("2500"));

        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(guest));
    }

    @Test
    void processPayment_beforeExpiry_confirmsBookingWithoutChangingInventory() {
        when(bookingRepository.findById(pendingBooking.getId())).thenReturn(Optional.of(pendingBooking));
        when(paymentRepository.findByTransactionId("pay_abc123")).thenReturn(Optional.empty());
        when(razorpayService.verifySignature("order_1", "pay_abc123", "sig_1")).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(UUID.randomUUID());
            return payment;
        });

        PaymentDTO request = paymentRequest("pay_abc123");
        PaymentDTO result = paymentService.processPayment(request, "guest@example.com");

        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertEquals(BookingStatus.CONFIRMED, pendingBooking.getBookingStatus());
        verify(bookingRepository).save(pendingBooking);
        // Payment confirm must not touch inventory — hold already happened at booking create
        verify(bookingService, never()).cancelAndReleaseInventory(any());
    }

    @Test
    void processPayment_afterExpiry_rejects() {
        pendingBooking.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(bookingRepository.findById(pendingBooking.getId())).thenReturn(Optional.of(pendingBooking));
        when(paymentRepository.findByTransactionId("pay_late")).thenReturn(Optional.empty());

        PaymentDTO request = paymentRequest("pay_late");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.processPayment(request, "guest@example.com"));

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Payment window expired", ex.getReason());
        verify(paymentRepository, never()).save(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createRazorpayOrder_afterExpiry_rejects() {
        pendingBooking.setExpiresAt(LocalDateTime.now().minusSeconds(5));
        when(bookingRepository.findById(pendingBooking.getId())).thenReturn(Optional.of(pendingBooking));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.createRazorpayOrder(pendingBooking.getId(), "guest@example.com"));

        assertEquals("Payment window expired", ex.getReason());
        verify(razorpayService, never()).createOrder(any(), any());
    }

    @Test
    void processPayment_idempotent_sameTransactionIdDoesNotCreateDuplicate() {
        Payment existing = new Payment();
        existing.setId(UUID.randomUUID());
        existing.setBooking(pendingBooking);
        existing.setTransactionId("pay_same");
        existing.setPaymentStatus(PaymentStatus.SUCCESS);
        existing.setPaidAt(LocalDateTime.now().minusMinutes(1));

        pendingBooking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(pendingBooking.getId())).thenReturn(Optional.of(pendingBooking));
        when(paymentRepository.findByTransactionId("pay_same")).thenReturn(Optional.of(existing));
        when(razorpayService.verifySignature("order_1", "pay_same", "sig_1")).thenReturn(true);

        PaymentDTO result = paymentService.processPayment(paymentRequest("pay_same"), "guest@example.com");

        assertEquals(existing.getId(), result.getId());
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refundPayment_usesSharedCancelAndReleaseInventory() {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setBooking(pendingBooking);
        payment.setTransactionId("pay_refund_me");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(guest));
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(razorpayService.refund("pay_refund_me", pendingBooking.getTotalPrice())).thenReturn("ref_123");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingService.cancelAndReleaseInventory(pendingBooking.getId())).thenReturn(new BookingDTO());

        PaymentDTO result = paymentService.refundPayment(payment.getId(), "guest@example.com");

        assertEquals(PaymentStatus.REFUNDED, result.getPaymentStatus());
        assertEquals("ref_123", result.getTransactionId());
        verify(bookingService).cancelAndReleaseInventory(pendingBooking.getId());
    }

    private PaymentDTO paymentRequest(String paymentId) {
        PaymentDTO dto = new PaymentDTO();
        dto.setBookingId(pendingBooking.getId());
        dto.setRazorpayOrderId("order_1");
        dto.setRazorpayPaymentId(paymentId);
        dto.setRazorpaySignature("sig_1");
        dto.setAmount(pendingBooking.getTotalPrice());
        return dto;
    }
}
