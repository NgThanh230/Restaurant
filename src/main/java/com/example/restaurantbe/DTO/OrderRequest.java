package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<OrderItemRequest> items;
    private String note;

    public OrderRequest(List<OrderItemRequest> items, String note) {
        this.items = items;
        this.note = note;
    }
}