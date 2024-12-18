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
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING,
        PREPARING,
        DELIVERING,
        COMPLETED,
        CANCELLED
    }


    public Order( User user, BigDecimal totalPrice, String deliveryAddress, OrderStatus orderStatus) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus = orderStatus;
    }

    public Order() {

    }
}
