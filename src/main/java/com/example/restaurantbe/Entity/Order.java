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
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "note")
    private String note;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // Gán giá trị mặc định
    }
    public enum OrderStatus {
        Pending,
        Preparing,
        Completed,
        Cancelled
    }
    public enum PaymentMethod {
        CASH, VNPAY, PAYPAL
    }

    public Order() {

    }
}
