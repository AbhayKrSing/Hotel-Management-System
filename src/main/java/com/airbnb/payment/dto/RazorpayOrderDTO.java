package com.airbnb.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class RazorpayOrderDTO {
    private String orderId;
    private UUID bookingId;
    private BigDecimal amount;
    private String currency;
    private String razorpayKeyId;

    public RazorpayOrderDTO() {}

    public RazorpayOrderDTO(String orderId, UUID bookingId, BigDecimal amount, String currency, String razorpayKeyId) {
        this.orderId = orderId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.razorpayKeyId = razorpayKeyId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRazorpayKeyId() { return razorpayKeyId; }
    public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }
}
