package com.airbnb.photo.dto;

import java.util.UUID;

public class UploadPhotoDTO {

	private UUID hotelId;
	private String photoUrl;
	private UUID roomId;
	public UUID getHotelId() {
		return hotelId;
	}
	public void setHotelId(UUID hotelId) {
		this.hotelId = hotelId;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public UUID getRoomId() {
		return roomId;
	}
	public void setRoomId(UUID roomId) {
		this.roomId = roomId;
	}
}
