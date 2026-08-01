package com.airbnb.shared.client;

import java.util.UUID;
import com.airbnb.hotel.dto.HotelDTO;

public interface HotelClient {
    HotelDTO getHotelById(UUID id);
    boolean hotelExists(UUID id);
}
