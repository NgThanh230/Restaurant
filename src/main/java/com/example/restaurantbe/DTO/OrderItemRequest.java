package com.example.restaurantbe.DTO;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequest {
    private Long dishId;       // ID món ăn
    private Integer quantity;


    public OrderItemRequest(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }
}
