package com.hotel.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private String checkInDate; // Format: YYYY-MM-DD
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private String checkOutDate; // Format: YYYY-MM-DD
    
    @NotNull(message = "Number of guests is required")
    @Min(1)
    private Integer numberOfGuests;
    
    private String specialRequests;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, CASH, ONLINE
}