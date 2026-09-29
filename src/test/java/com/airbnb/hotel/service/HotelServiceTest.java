package com.airbnb.hotel.service;

import com.airbnb.hotel.dto.HotelDTO;
import com.airbnb.hotel.enums.HotelStatus;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.airbnb.hotel.dto.RoomDTO;
import com.airbnb.hotel.model.Room;
import com.airbnb.hotel.repository.RoomRepository;
import com.airbnb.inventory.repository.InventoryRepository;
import com.airbnb.photo.client.PhotoClient;
import com.airbnb.photo.dto.UploadPhotoDTO;
import com.airbnb.user.dto.UserDTO;
import com.airbnb.user.client.impl.UserClientImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserClientImpl userClient;

    @Mock
    private PhotoClient photoClient;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private HotelService hotelService;

    private User testHost;
    private UserDTO testHostDTO;
    private Hotel testHotel;
    private HotelDTO testHotelDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testHost = new User();
        testHost.setId(UUID.randomUUID());
        testHost.setEmail("host@example.com");
        testHost.setName("Test Host");

        testHostDTO = new UserDTO();
        testHostDTO.setId(testHost.getId());
        testHostDTO.setEmail("host@example.com");
        testHostDTO.setFullName("Test Host");

        testHotel = new Hotel();
        testHotel.setId(UUID.randomUUID());
        testHotel.setName("Test Hotel");
        testHotel.setHostId(testHost.getId());
        testHotel.setStatus(HotelStatus.ACTIVE);

        testHotelDTO = new HotelDTO();
        testHotelDTO.setName("Test Hotel");
    }

    @Test
    void testCreateHotel_Success() {
        when(userClient.getUserByEmail(anyString())).thenReturn(testHostDTO);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        HotelDTO result = hotelService.createHotel(testHotelDTO, "host@example.com");

        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
        assertEquals(testHost.getId(), result.getHostId());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdateHotel_Success() {
        when(userClient.getUserByEmail(anyString())).thenReturn(testHostDTO);
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
        UserDTO otherUser = new UserDTO();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("other@example.com");

        when(userClient.getUserByEmail("other@example.com")).thenReturn(otherUser);
        when(hotelRepository.findById(testHotel.getId())).thenReturn(Optional.of(testHotel));

        HotelDTO updateDTO = new HotelDTO();
        updateDTO.setName("Hack Name");

        assertThrows(ResponseStatusException.class, () -> {
            hotelService.updateHotel(testHotel.getId(), updateDTO, "other@example.com");
        });

        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void testCreateRoom_PersistsInventory() {
        when(userClient.getUserByEmail("host@example.com")).thenReturn(testHostDTO);
        when(hotelRepository.findById(testHotel.getId())).thenReturn(Optional.of(testHotel));

        Room savedRoom = new Room();
        savedRoom.setId(UUID.randomUUID());
        savedRoom.setHotel(testHotel);
        savedRoom.setNoOfUnits(5);
        savedRoom.setPricePerNight(BigDecimal.valueOf(100));

        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setDescription("Deluxe Suite");
        roomDTO.setNoOfUnits(5);
        roomDTO.setPricePerNight(BigDecimal.valueOf(100));

        RoomDTO result = hotelService.createRoom(testHotel.getId(), roomDTO, "host@example.com");

        assertNotNull(result);
        verify(inventoryRepository, times(1)).saveAll(argThat(list -> {
            List<?> invList = (List<?>) list;
            return invList.size() == 90;
        }));
    }

    @Test
    void testAddRoomPhotos_Success() throws Exception {
        when(userClient.getUserByEmail("host@example.com")).thenReturn(testHostDTO);

        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setHotel(testHotel);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(cloudinaryService.uploadFile(any())).thenReturn("http://cloudinary.com/room.jpg");

        UploadPhotoDTO photoDTO = new UploadPhotoDTO();
        photoDTO.setRoomId(room.getId());
        photoDTO.setPhotoUrl("http://cloudinary.com/room.jpg");
        when(photoClient.createRoomPhoto(anyString(), any(UUID.class))).thenReturn(photoDTO);

        MultipartFile mockFile = mock(MultipartFile.class);
        List<UploadPhotoDTO> result = hotelService.addRoomPhotos(room.getId(), List.of(mockFile), "host@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(room.getId(), result.get(0).getRoomId());
        verify(photoClient, times(1)).createRoomPhoto(eq("http://cloudinary.com/room.jpg"), eq(room.getId()));
    }
}

