package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters, Setters, Constructors

    public Category(Long categoryId, LocalDateTime createdAt, String name) {
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.name = name;
    }

    public Category() {

    }
}
