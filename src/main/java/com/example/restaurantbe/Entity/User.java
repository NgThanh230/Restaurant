package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;
    private String passwordHash;
    private String role; // Customer, Restaurant, Admin, Delivery
    private String phoneNumber;
    private String address;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters, Setters, Constructors

    public User(Long userId, LocalDateTime createdAt, String phoneNumber, String address, String role, String passwordHash, String email, String name) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.passwordHash = passwordHash;
        this.email = email;
        this.name = name;
    }

    public User() {

    }
}
