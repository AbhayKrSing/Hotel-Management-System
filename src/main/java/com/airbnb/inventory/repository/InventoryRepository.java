package com.airbnb.inventory.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.airbnb.hotel.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByRoomId(UUID roomId);
    List<Inventory> findByRoomIdAndDateBetween(UUID roomId, Date start, Date end);
    Optional<Inventory> findByRoomIdAndDate(UUID roomId, Date date);
}
