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
    @Column(name = "dish_ingredient_id")
    private Long dishIngredientId;

    @ManyToOne
    @JoinColumn(name = "dish_id", referencedColumnName = "dish_id", nullable = false)
    private Dish dish;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "required_quantity", nullable = false)
    private BigDecimal requiredQuantity;

    @Column(name = "unit", nullable = false)
    private String unit;


    public DishIngredient(Long dishIngredientId, String unit, Ingredient ingredient, Dish dish, BigDecimal requiredQuantity) {
        this.dishIngredientId = dishIngredientId;
        this.unit = unit;
        this.ingredient = ingredient;
        this.dish = dish;
        this.requiredQuantity = requiredQuantity;
    }

    public DishIngredient() {

    }
}
