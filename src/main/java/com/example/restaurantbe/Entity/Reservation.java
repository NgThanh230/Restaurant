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

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    @Column(length = 100)
    private String guestName;

    @Column(length = 20)
    private String guestPhone;

    @Column(length = 100)
    private String guestEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_Id", nullable = false)
    private RestaurantTable table;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(name = "start_time" ,nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 20)
    private String status = "Pending"; // Default: Pending

    @Column(columnDefinition = "TEXT")
    private String notes;
}