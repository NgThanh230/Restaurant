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
    private BigDecimal totalPrice;
    private String note;
    private String orderStatus;
    private LocalDateTime createdAt;

    public OrderResponse(Long orderId,Long tableId, BigDecimal totalPrice, String note, String orderStatus, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.totalPrice = totalPrice;
        this.note = note;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }
}
