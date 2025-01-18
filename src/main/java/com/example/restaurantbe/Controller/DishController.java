package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.DishRequest;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    // API: Lấy tất cả món ăn
    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.getAllDishes();
    }

    // API: Lấy món ăn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        return dishService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // API: Thêm món ăn mới
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody DishRequest dishRequest) {
        // Gọi service để tạo món ăn
        Dish newDish = dishService.createDish(
                dishRequest.getName(),
                dishRequest.getDescription(),
                dishRequest.getPrice(),
                dishRequest.getImageUrl(),
                dishRequest.getCategoryId(),
                dishRequest.getRestaurantId());

        return ResponseEntity.ok(newDish);
    }

    // API: Cập nhật món ăn
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish updatedDish) {
        try {
            Dish updated = dishService.updateDish(id, updatedDish);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API: Xóa món ăn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}