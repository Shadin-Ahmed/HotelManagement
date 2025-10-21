package com.hotel.model;

import com.hotel.model.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // One payment belongs to one reservation
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }
    
    // Business logic methods
    public void processPayment() {
        if (paymentStatus == PaymentStatus.PENDING) {
            // Simulate payment processing
            this.paymentStatus = PaymentStatus.COMPLETED;
            this.transactionId = generateTransactionId();
            this.paymentDate = LocalDateTime.now();
        }
    }
    
    public void refund() {
        if (paymentStatus == PaymentStatus.COMPLETED) {
            this.paymentStatus = PaymentStatus.REFUNDED;
        }
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}