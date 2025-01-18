package com.example.restaurantbe.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Restaurant_Table") // Đặt tên bảng trong cơ sở dữ liệu
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tableId;

    @Column(nullable = false, unique = true, length = 10)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 20)
    private String status = "Available"; // Default: Available
}