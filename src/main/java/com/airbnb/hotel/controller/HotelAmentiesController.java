package com.airbnb.hotel.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.hotel.dto.AmenityRequestDTO;
import com.airbnb.hotel.model.HotelAmenities;
import com.airbnb.hotel.service.HotelService;

@RestController
@RequestMapping("/api/host/hotels/{hotelId}/amenities")
public class HotelAmentiesController {
	
        HotelService hotelService;
        
        public HotelAmentiesController(HotelService hotelService) {
        	this.hotelService=hotelService;
        }
	    
	    @PostMapping("/add")
	    public HotelAmenities addMasterAmenity(@PathVariable UUID hotelId, 
	                                         @RequestBody AmenityRequestDTO request) {
	        return null;
	    	// Hotel manager selects from master list
	       // return hotelService.addAmenity(hotelId, request.getMasterAmenityId());
	    }
	    
}
