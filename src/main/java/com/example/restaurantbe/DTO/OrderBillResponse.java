package com.example.restaurantbe.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderBillResponse {
    private Long orderId;
    private BigDecimal totalPrice;
    private String note;
    private String orderStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<OrderItemDTO> items;
    // getters/setters
}

