package com.airbnb.photo.client;

import java.util.UUID;

import com.airbnb.photo.dto.UploadPhotoDTO;

public interface PhotoClient {

	UploadPhotoDTO createHotelPhoto(String url,UUID hotelId);
}
