package com.hotel.model;

import com.hotel.model.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many reservations belong to one customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // Many reservations can be for one room (at different times)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    // One reservation has one payment
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
    
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
    
    // Business logic methods
    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
    
    public boolean canBeCancelled() {
        // Can cancel if status is PENDING or CONFIRMED and check-in is at least 24 hours away
        return (status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED) 
                && checkInDate.isAfter(LocalDate.now().plusDays(1));
    }
    
    public boolean canBeModified() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }
    
    public void confirm() {
        if (status == ReservationStatus.PENDING) {
            this.status = ReservationStatus.CONFIRMED;
            // Update room status
            if (room != null) {
                room.setStatus(com.hotel.model.enums.RoomStatus.RESERVED);
            }
        }
    }
    
    public void cancel() {
        if (canBeCancelled()) {
            this.status = ReservationStatus.CANCELLED;
            // Free up the room
            if (room != null) {
                room.setStatus(com.hotel.model.enums.RoomStatus.AVAILABLE);
            }
        }
    }
    
    public void checkIn() {
        if (status == ReservationStatus.CONFIRMED && LocalDate.now().equals(checkInDate)) {
            this.status = ReservationStatus.CHECKED_IN;
            if (room != null) {
                room.setStatus(com.hotel.model.enums.RoomStatus.OCCUPIED);
            }
        }
    }
    
    public void checkOut() {
        if (status == ReservationStatus.CHECKED_IN) {
            this.status = ReservationStatus.CHECKED_OUT;
            if (room != null) {
                room.setStatus(com.hotel.model.enums.RoomStatus.AVAILABLE);
            }
        }
    }
}