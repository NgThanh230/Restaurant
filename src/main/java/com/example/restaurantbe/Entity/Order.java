package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    private BigDecimal totalPrice;

    private String orderStatus; // Pending, Preparing, Delivering, Completed, Cancelled
    private String deliveryAddress;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters, Setters, Constructors

    public Order(Long orderId, User customer, BigDecimal totalPrice, String orderStatus, String deliveryAddress, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
    }

    public Order() {

    }
}
