package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // thời điểm khách sẽ đến

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // thời điểm tạo đặt bàn

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(name = "number_of_guests")
    private Integer numberOfGuests;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status")
    private String status; // Pending / Confirmed / Cancelled
}
