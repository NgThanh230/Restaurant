package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;
    private Long tableId;
    private String userName;
    private BigDecimal totalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;

    public OrderResponse(Long orderId,Long tableId,  String userName, BigDecimal totalPrice, String orderStatus, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }
}
