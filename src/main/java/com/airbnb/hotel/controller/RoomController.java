package com.airbnb.hotel.controller;

import com.airbnb.hotel.dto.RoomDTO;
import com.airbnb.hotel.service.HotelService;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.inventory.service.InventoryService;
import com.airbnb.shared.dto.ApiResponse;
import com.airbnb.shared.exceptions.ForbiddenException;
import com.airbnb.shared.exceptions.UnauthorizedException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.airbnb.photo.dto.UploadPhotoDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}")
public class RoomController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    public RoomController(HotelService hotelService, InventoryService inventoryService) {
        this.hotelService = hotelService;
        this.inventoryService = inventoryService;
    }

    private void verifyHostRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        boolean isHostOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST") || a.getAuthority().equals("ROLE_ADMIN"));
        if (!isHostOrAdmin) {
            throw new ForbiddenException("Access denied. Only hosts can manage rooms.");
        }
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDTO>> getRoomById(@PathVariable UUID roomId) {
        RoomDTO room = hotelService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Room fetched successfully", room));
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<ApiResponse<RoomDTO>> createRoom(@PathVariable UUID hotelId, @RequestBody RoomDTO roomDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        RoomDTO created = hotelService.createRoom(hotelId, roomDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Room created successfully", created));
    }

    @PostMapping("/rooms/{roomId}/photos")
    public ResponseEntity<ApiResponse<List<UploadPhotoDTO>>> uploadRoomPhotos(@PathVariable UUID roomId,
                                                                               @RequestParam("files") List<MultipartFile> files) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyHostRole(auth);
        List<UploadPhotoDTO> uploaded = hotelService.addRoomPhotos(roomId, files, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Room photos uploaded successfully", uploaded));
    }

    @GetMapping("/rooms/{roomId}/inventory")
    public ResponseEntity<ApiResponse<List<Inventory>>> getRoomInventory(
            @PathVariable UUID roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Inventory> inventoryList = inventoryService.getRoomInventory(roomId, start, end);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Room inventory fetched successfully", inventoryList));
    }
}

