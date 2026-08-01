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
	private UploadPhotoDTO convertToDTO(Photo hotelPhoto) {
		UploadPhotoDTO uploadPhotoDto=new UploadPhotoDTO();
		uploadPhotoDto.setHotelId(hotelPhoto.getHotelId());
		uploadPhotoDto.setPhotoUrl(hotelPhoto.getPhotoUrl());
		return uploadPhotoDto;
		
	}
   
}
