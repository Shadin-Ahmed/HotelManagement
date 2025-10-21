package com.hotel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends User {
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    private String department;
    
    @Override
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Admin(String username, String password, String email, String phone,
                String firstName, String lastName, String department) {
        super();
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setPhone(phone);
        this.setRole(Role.ADMIN);
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }
}