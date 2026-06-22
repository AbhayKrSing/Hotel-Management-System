package com.airbnb.payment.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.airbnb.booking.model.Booking;
import com.airbnb.payment.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_payment_booking")
public class Payment {
private UUID id;
private String transactionId;
private PaymentStatus paymentStatus;
private Booking booking;
private LocalDateTime paidAt;
private LocalDateTime createdAt;

@Id
@Column(name="C_Payment_Id")
public UUID getId() {
	return id;
}
public void setId(UUID id) {
	this.id = id;
}

@Column(name = "C_Transaction_Id") //Comes from stripe,razorPay etc.
public String getTransactionId() {
	return transactionId;
}
public void setTransactionId(String transactionId) {
	this.transactionId = transactionId;
}

@Enumerated(EnumType.STRING)
@Column(name ="C_Payment_Status")
public PaymentStatus getPaymentStatus() {
	return paymentStatus;
}
public void setPaymentStatus(PaymentStatus paymentStatus) {
	this.paymentStatus = paymentStatus;
}

@ManyToOne
@JoinColumn(name="C_Booking_Id")
@JsonBackReference
public Booking getBooking() {
	return booking;
}
public void setBooking(Booking booking) {
	this.booking = booking;
}
public LocalDateTime getPaidAt() {
	return paidAt;
}
public void setPaidAt(LocalDateTime paidAt) {
	this.paidAt = paidAt;
}
public LocalDateTime getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}


}
