package com.airbnb.hotel.controller;

import com.airbnb.hotel.dto.RoomDTO;
import com.airbnb.hotel.service.HotelService;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.inventory.service.InventoryService;
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
@RequestMapping("/api")
public class RoomController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    public RoomController(HotelService hotelService, InventoryService inventoryService) {
        this.hotelService = hotelService;
        this.inventoryService = inventoryService;
    }

    private void verifyHostRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        boolean isHostOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST") || a.getAuthority().equals("ROLE_ADMIN"));
        if (!isHostOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only hosts can manage rooms.");
        }
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<RoomDTO> createRoom(@PathVariable UUID hotelId, @RequestBody RoomDTO roomDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);

        RoomDTO created = hotelService.createRoom(hotelId, roomDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    @PostMapping("/rooms/{roomId}/photos")
//    public ResponseEntity<RoomDTO> uploadRoomPhotos(@PathVariable UUID roomId, @RequestParam("files") List<MultipartFile> files) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        verifyHostRole(auth);
//
//        try {
//            RoomDTO updated = hotelService.addRoomPhotos(roomId, files, auth.getName());
//            return ResponseEntity.ok(updated);
//        } catch (IOException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload photos", e);
//        }
//    }

    @GetMapping("/rooms/{roomId}/inventory")
    public ResponseEntity<List<Inventory>> getRoomInventory(
            @PathVariable UUID roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Inventory> inventoryList = inventoryService.getRoomInventory(roomId, start, end);
        return ResponseEntity.ok(inventoryList);
    }
}
