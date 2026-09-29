package com.airbnb.hotel.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.hotel.dto.AmenityRequestDTO;
import com.airbnb.hotel.model.HotelAmenities;
import com.airbnb.hotel.service.HotelService;
import com.airbnb.shared.dto.ApiResponse;

@RestController
@RequestMapping("/api/${api.version}/host/hotels/{hotelId}/amenities")
public class HotelAmentiesController {

        HotelService hotelService;

        public HotelAmentiesController(HotelService hotelService) {
        	this.hotelService = hotelService;
        }

        @PostMapping("/add")
        public ResponseEntity<ApiResponse<HotelAmenities>> addMasterAmenity(@PathVariable UUID hotelId,
                                                     @RequestBody AmenityRequestDTO request) {
            // Hotel manager selects from master list
            // HotelAmenities result = hotelService.addAmenity(hotelId, request.getMasterAmenityId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED.value(), "Amenity added successfully", null));
        }

}
