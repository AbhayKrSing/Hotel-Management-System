package com.airbnb.hotel.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.airbnb.hotel.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByHotelId(UUID hotelId);
}
