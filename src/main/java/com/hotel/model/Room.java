package com.hotel.model;

import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_number", unique = true, nullable = false)
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;
    
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(nullable = false)
    private Integer capacity;
    
    private Integer floor;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;
    
    private String amenities; // Can be JSON or comma-separated
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Polymorphic method - calculates price based on room type and duration
    public BigDecimal calculateTotalPrice(int numberOfNights) {
        BigDecimal multiplier = getTypeMultiplier();
        return basePrice.multiply(multiplier).multiply(new BigDecimal(numberOfNights));
    }
    
    // Helper method for price calculation
    private BigDecimal getTypeMultiplier() {
        return switch (roomType) {
            case STANDARD -> new BigDecimal("1.0");
            case DELUXE -> new BigDecimal("1.5");
            case SUITE -> new BigDecimal("2.0");
        };
    }
    
    // Check if room is available
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }
    
    // Apply discount (business logic example)
    public BigDecimal applyDiscount(int numberOfNights, BigDecimal discountPercentage) {
        BigDecimal totalPrice = calculateTotalPrice(numberOfNights);
        BigDecimal discount = totalPrice.multiply(discountPercentage).divide(new BigDecimal("100"));
        return totalPrice.subtract(discount);
    }
}