package com.example.restaurantbe.Repository;

import com.example.restaurantbe.Entity.Category;
import com.example.restaurantbe.Entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findById(Long dishId);
    boolean existsByCategory(Category category);
}
