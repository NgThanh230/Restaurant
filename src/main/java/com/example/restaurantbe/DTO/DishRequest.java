package com.example.restaurantbe.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Long categoryId;   // ID của Category
    private Long restaurantId; // ID của User (restaurant)

    // Getters and setters
}
