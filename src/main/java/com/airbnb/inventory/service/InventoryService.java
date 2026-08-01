package com.airbnb.inventory.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.airbnb.hotel.model.Inventory;

public interface InventoryService {
    List<Inventory> getRoomInventory(UUID roomId, Date start, Date end);
    void blockInventory(UUID roomId, Date start, Date end);
    void setSeasonalPricing(UUID roomId, Date start, Date end, BigDecimal price);
}
