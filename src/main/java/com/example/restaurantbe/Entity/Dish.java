package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dishes")

public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishId;

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private com.example.restaurantbe.Entity.Category category;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private User restaurant; // Liên kết với User (Role = Restaurant)

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters, Setters, Constructors


    public Dish(Long dishId, String name, String description, BigDecimal price, String imageUrl, Category category, User restaurant, LocalDateTime createdAt) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
    }

    public Dish() {

    }

    public Long getDishId() {
        return dishId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getRestaurant() {
        return restaurant;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setRestaurant(User restaurant) {
        this.restaurant = restaurant;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
