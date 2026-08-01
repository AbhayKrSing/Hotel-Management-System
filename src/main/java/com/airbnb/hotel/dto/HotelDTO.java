package com.airbnb.hotel.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.airbnb.hotel.enums.HotelStatus;

public class HotelDTO {
	private UUID id;
	private String name;
	private String description;
	private String address;
	private String city;
	private String country;
	private String zipCode;
	private String phoneNumber;
	private HotelStatus status;
	private UUID hostId;
	private String hostName;
	private String hostEmail;
	private String mainPhotoUrl;
	private String cancellationPolicy;
	private String checkInTime;
	private String checkOutTime;
	private Integer starRating;
	private Boolean isVerified;
	private String amenities;
	private Double latitude;
	private Double longitude;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public HotelStatus getStatus() {
		return status;
	}

	public void setStatus(HotelStatus status) {
		this.status = status;
	}

	public UUID getHostId() {
		return hostId;
	}

	public void setHostId(UUID hostId) {
		this.hostId = hostId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostEmail() {
		return hostEmail;
	}

	public void setHostEmail(String hostEmail) {
		this.hostEmail = hostEmail;
	}

	public String getMainPhotoUrl() {
		return mainPhotoUrl;
	}

	public void setMainPhotoUrl(String mainPhotoUrl) {
		this.mainPhotoUrl = mainPhotoUrl;
	}

	public String getCancellationPolicy() {
		return cancellationPolicy;
	}

	public void setCancellationPolicy(String cancellationPolicy) {
		this.cancellationPolicy = cancellationPolicy;
	}

	public String getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	public String getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public Integer getStarRating() {
		return starRating;
	}

	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
