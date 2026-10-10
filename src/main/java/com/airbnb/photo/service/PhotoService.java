package com.airbnb.photo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.airbnb.photo.model.Photo;
import com.airbnb.photo.repository.PhotoRepository;
import com.airbnb.shared.exceptions.ResourceNotFoundException;

@Service
public class PhotoService {
 	
	private PhotoRepository photoRepository;
    	
	public PhotoService(PhotoRepository photoRepository) {
		this.photoRepository=photoRepository;
	}
	
	public Photo createHotelPhoto(String url, UUID hotelId) {
		Photo photo = new Photo();
		photo.setHotelId(hotelId);
		photo.setPhotoUrl(url);
	    return photoRepository.save(photo);
	}

	public Photo createRoomPhoto(String url, UUID roomId) {
		Photo photo = new Photo();
		photo.setRoomId(roomId);
		photo.setPhotoUrl(url);
		return photoRepository.save(photo);
	}

	public List<Photo> getHotelPhotos(UUID hotelId) {
		return photoRepository.findByHotelId(hotelId);
	}

	public void deletePhoto(UUID photoId) {
		if (!photoRepository.existsById(photoId)) {
			throw new ResourceNotFoundException("Photo not found with id: " + photoId);
		}
		photoRepository.deleteById(photoId);
	}
}
