package com.airbnb.photo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.airbnb.photo.model.Photo;
import com.airbnb.photo.repository.PhotoRepository;

@Service
public class PhotoService {
	 
	private PhotoRepository photoRepository;
    	
	public PhotoService(PhotoRepository photoRepository) {
		this.photoRepository=photoRepository;
	}
	
	public Photo createHotelPhoto(String url,UUID hotelId) {
		Photo photo=new Photo();
		photo.setHotelId(hotelId);
		photo.setPhotoUrl(url);
	   return photoRepository.save(photo);
	}

}
