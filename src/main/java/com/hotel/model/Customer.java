package com.hotel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Customer extends User {
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    private String address;
    
    private String city;
    
    private String country;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    // One customer can have multiple reservations
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
    
    @Override
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Customer(String username, String password, String email, String phone, 
                   String firstName, String lastName) {
        super();
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setPhone(phone);
        this.setRole(Role.CUSTOMER);
        this.firstName = firstName;
        this.lastName = lastName;
    }
}