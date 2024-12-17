package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    private String name;
    private BigDecimal quantity;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private User restaurant;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters, Setters, Constructors

    public Ingredient(Long ingredientId, String name, BigDecimal quantity, LocalDateTime createdAt, String unit, User restaurant, LocalDateTime updatedAt) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.unit = unit;
        this.restaurant = restaurant;
        this.updatedAt = updatedAt;
    }

    public Ingredient() {

    }
}

