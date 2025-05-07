package com.example.restaurantbe.DTO;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderItemDTO {
    private String dishName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;
}
