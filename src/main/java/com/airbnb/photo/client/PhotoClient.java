package com.airbnb.photo.client;

import java.util.List;
import java.util.UUID;

import com.airbnb.photo.dto.UploadPhotoDTO;

public interface PhotoClient {

	UploadPhotoDTO createHotelPhoto(String url, UUID hotelId);
	UploadPhotoDTO createRoomPhoto(String url, UUID roomId);
	List<UploadPhotoDTO> getHotelPhotos(UUID hotelId);
	void deletePhoto(UUID photoId);

}
