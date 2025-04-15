package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private Long tableId;
    private List<OrderItemRequest> items;

    public OrderRequest(Long userId, List<OrderItemRequest> items, Long TableId) {
        this.userId = userId;
        this.items = items;
        this.tableId = TableId;
    }
}
