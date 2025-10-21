package com.hotel.model;

import java.math.BigDecimal;

public class Room {
    public enum RoomType {
        SINGLE("Single Room", 1),
        DOUBLE("Double Room", 2),
        SUITE("Suite", 4),
        DELUXE("Deluxe Room", 3);
        
        private final String displayName;
        private final int capacity;
        
        RoomType(String displayName, int capacity) {
            this.displayName = displayName;
            this.capacity = capacity;
        }
        
        public String getDisplayName() { return displayName; }
        public int getCapacity() { return capacity; }
    }
    
    private int id;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private String description;
    private String amenities;
    private boolean isAvailable;
    
    public Room() {}
    
    public Room(String roomNumber, RoomType roomType, BigDecimal pricePerNight, String description, String amenities) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.amenities = amenities;
        this.isAvailable = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public String getRoomTypeDisplay() {
        return roomType.getDisplayName();
    }
    
    public int getCapacity() {
        return roomType.getCapacity();
    }
}