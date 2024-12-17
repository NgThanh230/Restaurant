package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "dish_ingredients")
@Getter
@Setter
public class DishIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishIngredientId;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private BigDecimal requiredQuantity; // Số lượng nguyên liệu cần
    private String unit;

    // Getters, Setters, Constructors

    public DishIngredient(BigDecimal requiredQuantity, Long dishIngredientId, Dish dish, Ingredient ingredient, String unit) {
        this.requiredQuantity = requiredQuantity;
        this.dishIngredientId = dishIngredientId;
        this.dish = dish;
        this.ingredient = ingredient;
        this.unit = unit;
    }

    public DishIngredient() {

    }
}
