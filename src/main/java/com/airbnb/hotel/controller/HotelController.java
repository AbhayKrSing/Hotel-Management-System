package com.airbnb.hotel.controller;

import com.airbnb.hotel.dto.HotelDTO;
import com.airbnb.hotel.dto.RoomDTO;
import com.airbnb.hotel.service.HotelService;
import com.airbnb.photo.dto.UploadPhotoDTO;
import com.airbnb.user.enums.Roles;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    private void verifyHostRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        boolean isHostOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_"+Roles.HOST.getDisplayName()) || a.getAuthority().equals("ROLE_"+Roles.ADMIN.getDisplayName()));
        if (!isHostOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only hosts can manage hotels.");
        }
    }

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        HotelDTO created = hotelService.createHotel(hotelDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-hotels")
    public ResponseEntity<List<HotelDTO>> getMyHotels() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        List<HotelDTO> hotels = hotelService.getHostHotels(auth.getName());
        return ResponseEntity.ok(hotels);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable UUID id, @RequestBody HotelDTO hotelDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        HotelDTO updated = hotelService.updateHotel(id, hotelDTO, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<List<UploadPhotoDTO>> uploadPhotos(@PathVariable UUID id,
                                                  @RequestParam("files") List<MultipartFile> files) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        List<UploadPhotoDTO> updated = hotelService.addHotelPhotos(id, files, auth.getName());
		return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable UUID id) {
        HotelDTO hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    /**
     * GET /api/hotels/search?city=Mumbai&keyword=resort&guestCount=2
     * Public search by city and/or keyword across ACTIVE hotels.
     */
    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer guestCount) {
        List<HotelDTO> results = hotelService.searchHotels(city, keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/hotels/{id}/available-rooms?checkIn=yyyy-MM-dd&checkOut=yyyy-MM-dd&guestCount=2
     * Returns rooms with sufficient inventory availability for the requested date range.
     */
    @GetMapping("/{id}/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkIn,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOut,
            @RequestParam(defaultValue = "1") int guestCount) {
        List<RoomDTO> rooms = hotelService.getAvailableRooms(id, checkIn, checkOut, guestCount);
        return ResponseEntity.ok(rooms);
    }
    
    
}
