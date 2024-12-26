package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private String deliveryAddress;
    private List<OrderItemRequest> items;

    public OrderRequest(Long userId, List<OrderItemRequest> items, String deliveryAddress) {
        this.userId = userId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
    }
}
