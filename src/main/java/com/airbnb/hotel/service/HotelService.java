package com.airbnb.hotel.service;

import com.airbnb.hotel.dto.HotelDTO;
import com.airbnb.hotel.dto.RoomDTO;
import com.airbnb.hotel.enums.HotelStatus;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.hotel.model.Room;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.hotel.repository.RoomRepository;
import com.airbnb.inventory.repository.InventoryRepository;
import com.airbnb.photo.client.PhotoClient;
import com.airbnb.photo.dto.UploadPhotoDTO;
import com.airbnb.photo.model.Photo;
import com.airbnb.user.client.impl.UserClientImpl;
import com.airbnb.user.dto.UserDTO;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import com.airbnb.shared.client.UserClient;
import com.airbnb.shared.exceptions.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserClient userClient;
    private final PhotoClient photoClient;
    private final CloudinaryService cloudinaryService;
    private final InventoryRepository inventoryRepository;

    public HotelService(HotelRepository hotelRepository,
                        RoomRepository roomRepository,
                        UserClientImpl userClient,
                        CloudinaryService cloudinaryService,
                        InventoryRepository inventoryRepository,PhotoClient photoClient) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.userClient = userClient;
        this.cloudinaryService = cloudinaryService;
        this.inventoryRepository = inventoryRepository;
        this.photoClient=photoClient;
    }

    public HotelDTO createHotel(HotelDTO dto, String hostEmail) {
        UserDTO userDto = userClient.getUserByEmail(hostEmail);
        if(userDto == null) {
        	throw new ResourceNotFoundException(hostEmail);
        }

        Hotel hotel = new Hotel();
        mapDTOToEntity(dto, hotel);
        hotel.setHostId(userDto.getId());
        
        // Default status to ACTIVE if not provided
        if (dto.getStatus() == null) {
            hotel.setStatus(HotelStatus.ACTIVE);
        } else {
            hotel.setStatus(dto.getStatus());
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDTO(savedHotel);
    }

    public List<HotelDTO> getHostHotels(String hostEmail) {
        UserDTO host = userClient.getUserByEmail(hostEmail);
        if(host == null) {
        	throw new ResourceNotFoundException(hostEmail);
        }        

        List<Hotel> hotels = hotelRepository.findByHostId(host.getId());
        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO updateHotel(UUID id, HotelDTO dto, String hostEmail) {
        UserDTO host = userClient.getUserByEmail(hostEmail);
        if(host == null) {
        	throw new ResourceNotFoundException(hostEmail);
        }        

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        // Validate Ownership: Only the owner can edit their hotel		
        if (!hotel.getHostId().equals(host.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can edit this hotel");
        }

        mapDTOToEntity(dto, hotel);
        
        if (dto.getStatus() != null) {
            hotel.setStatus(dto.getStatus());
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        return convertToDTO(updatedHotel);
    }



    public HotelDTO getHotelById(UUID id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return convertToDTO(hotel);
    }

    public List<HotelDTO> searchHotels(String city, String keyword) {
        List<Hotel> hotels;
        if (city != null && !city.isBlank()) {
            hotels = hotelRepository.findByCityAndStatus(city, HotelStatus.ACTIVE);
        } else if (keyword != null && !keyword.isBlank()) {
            hotels = hotelRepository.searchHotels(keyword).stream()
                    .filter(h -> h.getStatus() == HotelStatus.ACTIVE)
                    .collect(Collectors.toList());
        } else {
            hotels = hotelRepository.findAll().stream()
                    .filter(h -> h.getStatus() == HotelStatus.ACTIVE)
                    .collect(Collectors.toList());
        }
        return hotels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRooms(UUID hotelId, Date checkIn, Date checkOut, int guestCount) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        List<Date> dates = getDatesBetween(checkIn, checkOut);
        if (dates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }

        return hotel.getRooms().stream()
                .filter(room -> room.getGuestCapacity() >= guestCount)
                .filter(room -> isRoomAvailable(room, dates))
                .map(this::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailable(Room room, List<Date> dates) {
        for (Date date : dates) {
            boolean hasStock = inventoryRepository.findByRoomIdAndDate(room.getId(), date)
                    .map(inv -> inv.getAvailableUnits() > 0)
                    .orElse(false);
            if (!hasStock) return false;
        }
        return true;
    }

    private List<Date> getDatesBetween(Date start, Date end) {
        List<Date> dates = new ArrayList<>();
        if (start == null || end == null || !start.before(end)) return dates;
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.HOUR_OF_DAY, 0); endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0); endCal.set(Calendar.MILLISECOND, 0);
        while (cal.before(endCal)) {
            dates.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    // --- Room Methods ---

    public RoomDTO createRoom(UUID hotelId, RoomDTO dto, String hostEmail) {
        UserDTO host = userClient.getUserByEmail(hostEmail);
       
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        if (!hotel.getHostId().equals(host.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can add rooms to this hotel");
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setDescription(dto.getDescription());
        room.setRoomTypes(dto.getRoomTypes());
        room.setGuestCapacity(dto.getGuestCapacity());
        room.setBedType(dto.getBedType());
        room.setStatus(HotelStatus.ACTIVE);
        room.setNoOfUnits(dto.getNoOfUnits());
        room.setPricePerNight(dto.getPricePerNight());

        Room savedRoom = roomRepository.save(room);
        // Pre-populate inventory for next 90 days
        List<Inventory> inventories = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // Zero out time fields to make date matching easier
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 90; i++) {
            Inventory inv = new Inventory();
            inv.setRoomId(savedRoom.getId());
            inv.setDate(cal.getTime());
            inv.setAvailableUnits(dto.getNoOfUnits());
            inv.setPricePerNight(dto.getPricePerNight());
            inventories.add(inv);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return convertToRoomDTO(savedRoom);
    }

    public RoomDTO convertToRoomDTO(Room room) {
        if (room == null) {
            return null;
        }
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setDescription(room.getDescription());
        dto.setRoomTypes(room.getRoomTypes());
        dto.setGuestCapacity(room.getGuestCapacity());
        dto.setBedType(room.getBedType());
        dto.setStatus(room.getStatus());
        dto.setNoOfUnits(room.getNoOfUnits());
        dto.setPricePerNight(room.getPricePerNight());
        return dto;
    }

    // --- Helper Mappers ---

    private void mapDTOToEntity(HotelDTO dto, Hotel hotel) {
        hotel.setName(dto.getName());
        hotel.setDescription(dto.getDescription());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setZipCode(dto.getZipCode());
        hotel.setPhoneNumber(dto.getPhoneNumber());
        hotel.setCancellationPolicy(dto.getCancellationPolicy());
        hotel.setCheckInTime(dto.getCheckInTime());
        hotel.setCheckOutTime(dto.getCheckOutTime());
        hotel.setStarRating(dto.getStarRating());
        hotel.setIsVerified(dto.getIsVerified());
        hotel.setLatitude(dto.getLatitude());
        hotel.setLongitude(dto.getLongitude());
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        if (hotel == null) {
            return null;
        }
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setAddress(hotel.getAddress());
        dto.setCity(hotel.getCity());
        dto.setCountry(hotel.getCountry());
        dto.setZipCode(hotel.getZipCode());
        dto.setPhoneNumber(hotel.getPhoneNumber());
        dto.setStatus(hotel.getStatus());
        dto.setCancellationPolicy(hotel.getCancellationPolicy());
        dto.setCheckInTime(hotel.getCheckInTime());
        dto.setCheckOutTime(hotel.getCheckOutTime());
        dto.setStarRating(hotel.getStarRating());
        dto.setIsVerified(hotel.getIsVerified());
        dto.setLatitude(hotel.getLatitude());
        dto.setLongitude(hotel.getLongitude());
        dto.setCreatedAt(hotel.getCreatedAt());
        dto.setUpdatedAt(hotel.getUpdatedAt());

        if (hotel.getHostId() != null) {
            dto.setHostId(hotel.getHostId());
        }

    

        return dto;
    }

	public List<UploadPhotoDTO> addHotelPhotos(UUID id, List<MultipartFile> files, String name) {
		List<UploadPhotoDTO> uploadPhotoList=new ArrayList<>();
		Hotel hotelById = hotelRepository.findById(id)
			    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
		files.forEach((file)->{
			try {
				String uploadFile = cloudinaryService.uploadFile(file);
				uploadPhotoList.add(photoClient.createHotelPhoto(uploadFile, hotelById.getId())) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return uploadPhotoList;
	}
}
