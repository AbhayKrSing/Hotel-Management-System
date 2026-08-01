package com.airbnb.hotel.client.impl;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.airbnb.hotel.dto.HotelDTO;
import com.airbnb.hotel.service.HotelService;
import com.airbnb.shared.client.HotelClient;

@Service
public class HotelClientImpl implements HotelClient {

    private final HotelService hotelService;

    public HotelClientImpl(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Override
    public HotelDTO getHotelById(UUID id) {
        return hotelService.getHotelById(id);
    }

    @Override
    public boolean hotelExists(UUID id) {
        try {
            hotelService.getHotelById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
