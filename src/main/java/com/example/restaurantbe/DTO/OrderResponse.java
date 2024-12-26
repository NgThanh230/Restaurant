package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String userName;
    private BigDecimal totalPrice;
    private String orderStatus;
    private String deliveryAddress;
    private LocalDateTime createdAt;

    public OrderResponse(Long orderId, Long userId, String userName, BigDecimal totalPrice, String orderStatus, String deliveryAddress, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
    }
}
