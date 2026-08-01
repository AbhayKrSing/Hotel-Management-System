package com.airbnb.hotel.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.airbnb.hotel.enums.HotelStatus;

public class RoomDTO {
    private UUID id;
    private UUID hotelId;
    private String description;
    private String roomTypes;
    private Integer guestCapacity;
    private String bedType;
    private HotelStatus status;
    private Integer noOfUnits;
    private BigDecimal pricePerNight;
    private List<String> photoUrls;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getHotelId() { return hotelId; }
    public void setHotelId(UUID hotelId) { this.hotelId = hotelId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRoomTypes() { return roomTypes; }
    public void setRoomTypes(String roomTypes) { this.roomTypes = roomTypes; }

    public Integer getGuestCapacity() { return guestCapacity; }
    public void setGuestCapacity(Integer guestCapacity) { this.guestCapacity = guestCapacity; }

    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    public HotelStatus getStatus() { return status; }
    public void setStatus(HotelStatus status) { this.status = status; }

    public Integer getNoOfUnits() { return noOfUnits; }
    public void setNoOfUnits(Integer noOfUnits) { this.noOfUnits = noOfUnits; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
}
