package com.airbnb.hotel.service;

import com.airbnb.hotel.dto.HotelDTO;
import com.airbnb.hotel.enums.HotelStatus;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private HotelService hotelService;

    private User testHost;
    private Hotel testHotel;
    private HotelDTO testHotelDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testHost = new User();
        testHost.setId(UUID.randomUUID());
        testHost.setEmail("host@example.com");
        testHost.setName("Test Host");

        testHotel = new Hotel();
        testHotel.setId(UUID.randomUUID());
        testHotel.setName("Test Hotel");
       // testHotel.setHost(testHost);
        testHotel.setStatus(HotelStatus.ACTIVE);

        testHotelDTO = new HotelDTO();
        testHotelDTO.setName("Test Hotel");
    }

    @Test
    void testCreateHotel_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testHost));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        HotelDTO result = hotelService.createHotel(testHotelDTO, "host@example.com");

        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
        assertEquals(testHost.getId(), result.getHostId());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdateHotel_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testHost));
        when(hotelRepository.findById(any(UUID.class))).thenReturn(Optional.of(testHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        HotelDTO updateDTO = new HotelDTO();
        updateDTO.setName("Updated Hotel Name");
        updateDTO.setStatus(HotelStatus.INACTIVE);

        HotelDTO result = hotelService.updateHotel(testHotel.getId(), updateDTO, "host@example.com");

        assertNotNull(result);
        assertEquals(HotelStatus.INACTIVE, result.getStatus());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdateHotel_Forbidden_NotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("other@example.com");

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(hotelRepository.findById(testHotel.getId())).thenReturn(Optional.of(testHotel));

        HotelDTO updateDTO = new HotelDTO();
        updateDTO.setName("Hack Name");

        assertThrows(ResponseStatusException.class, () -> {
            hotelService.updateHotel(testHotel.getId(), updateDTO, "other@example.com");
        });

        verify(hotelRepository, never()).save(any(Hotel.class));
    }
}
