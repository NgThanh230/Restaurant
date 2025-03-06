package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_Id", nullable = false)
    private RestaurantTable table;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false, length = 20)
    private String status = "Pending"; // Default: Pending

    @Column(columnDefinition = "TEXT")
    private String notes;
}