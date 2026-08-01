package com.airbnb.inventory.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.inventory.repository.InventoryRepository;
import com.airbnb.inventory.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Inventory> getRoomInventory(UUID roomId, Date start, Date end) {
        return inventoryRepository.findByRoomIdAndDateBetween(roomId, start, end);
    }

    @Override
    public void blockInventory(UUID roomId, Date start, Date end) {
        List<Inventory> inventories = inventoryRepository.findByRoomIdAndDateBetween(roomId, start, end);
        for (Inventory inv : inventories) {
            inv.setAvailableUnits(0);
        }
        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void setSeasonalPricing(UUID roomId, Date start, Date end, BigDecimal price) {
        List<Inventory> inventories = inventoryRepository.findByRoomIdAndDateBetween(roomId, start, end);
        for (Inventory inv : inventories) {
            inv.setPricePerNight(price);
        }
        inventoryRepository.saveAll(inventories);
    }
}
