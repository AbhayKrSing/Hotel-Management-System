package com.airbnb.photo.client;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.airbnb.photo.dto.UploadPhotoDTO;
import com.airbnb.photo.model.Photo;
import com.airbnb.photo.service.PhotoService;

@Service
public class PhotoClientImpl  implements PhotoClient {
    
	PhotoService photoService;
	
	public PhotoClientImpl(PhotoService photoService) {
		this.photoService=photoService;
	}
	@Override
	public UploadPhotoDTO createHotelPhoto(String url, UUID hotelId) {
		Photo hotelPhoto = photoService.createHotelPhoto(url, hotelId);
		return convertToDTO(hotelPhoto);
	}

	@Override
	public UploadPhotoDTO createRoomPhoto(String url, UUID roomId) {
		Photo roomPhoto = photoService.createRoomPhoto(url, roomId);
		return convertToDTO(roomPhoto);
	}

	private UploadPhotoDTO convertToDTO(Photo photo) {
		UploadPhotoDTO uploadPhotoDto=new UploadPhotoDTO();
		uploadPhotoDto.setHotelId(photo.getHotelId());
		uploadPhotoDto.setRoomId(photo.getRoomId());
		uploadPhotoDto.setPhotoUrl(photo.getPhotoUrl());
		return uploadPhotoDto;
	}

   
}
