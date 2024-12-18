package com.example.restaurantbe.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id", referencedColumnName = "user_id", nullable = false)
    private User deliveryPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public Delivery() {

    }

    public enum DeliveryStatus {
        PENDING,
        IN_TRANSIT,
        DELIVERED,
        FAILED
    }

    public Delivery(Order order, User deliveryPerson, DeliveryStatus deliveryStatus) {
        this.order = order;
        this.deliveryPerson = deliveryPerson;
        this.deliveryStatus = deliveryStatus;
    }
}

