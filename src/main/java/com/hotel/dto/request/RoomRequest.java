package com.hotel.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;
    
    @NotNull(message = "Room type is required")
    private String roomType; // STANDARD, DELUXE, SUITE
    
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double basePrice;
    
    @NotNull(message = "Capacity is required")
    @Min(1)
    private Integer capacity;
    
    private Integer floor;
    private String description;
    private String amenities;
    private String imageUrl;
}