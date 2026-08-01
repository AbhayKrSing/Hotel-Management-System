package com.airbnb.inventory.controller;

import com.airbnb.hotel.model.Inventory;
import com.airbnb.inventory.service.InventoryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    private void requireHost(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        boolean isHostOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST") || a.getAuthority().equals("ROLE_ADMIN"));
        if (!isHostOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only hosts can manage inventory");
        }
    }

    /**
     * GET /api/inventory/{roomId}?start=yyyy-MM-dd&end=yyyy-MM-dd
     * Retrieve availability calendar for a room within a date range.
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<Inventory>> getRoomInventory(
            @PathVariable UUID roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Inventory> inventory = inventoryService.getRoomInventory(roomId, start, end);
        return ResponseEntity.ok(inventory);
    }

    /**
     * PUT /api/inventory/block?roomId=...&start=...&end=...
     * Host blocks dates for a room (availableUnits = 0 for maintenance, etc.)
     */
    @PutMapping("/block")
    public ResponseEntity<Void> blockInventory(
            @RequestParam UUID roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        requireHost(auth);
        inventoryService.blockInventory(roomId, start, end);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/inventory/price?roomId=...&start=...&end=...&price=...
     * Host sets custom/seasonal price for a room's date range.
     */
    @PutMapping("/price")
    public ResponseEntity<Void> setSeasonalPrice(
            @RequestParam UUID roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
            @RequestParam BigDecimal price) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        requireHost(auth);
        inventoryService.setSeasonalPricing(roomId, start, end, price);
        return ResponseEntity.ok().build();
    }
}
