package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredient_logs")
@Getter
@Setter
public class IngredientLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "change_amount", nullable = false)
    private BigDecimal changeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private LogType logType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum LogType {
        IMPORT,
        EXPORT
    }

    // Default constructor (required by Hibernate)
    public IngredientLog() {}

    // Constructor with parameters
    public IngredientLog(Ingredient ingredient, BigDecimal changeAmount, LogType logType, String description) {
        this.ingredient = ingredient;
        this.changeAmount = changeAmount;
        this.logType = logType;
        this.description = description;
    }


}
