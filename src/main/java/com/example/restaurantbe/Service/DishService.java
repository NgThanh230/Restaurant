package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    // Lấy tất cả món ăn
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    // Lấy món ăn theo ID
    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findByDishId(id);
    }

    // Thêm món ăn mới
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    // Cập nhật món ăn
    public Dish updateDish(Long id, Dish updatedDish) {
        return dishRepository.findByDishId(id).map(dish -> {
            dish.setName(updatedDish.getName());
            dish.setDescription(updatedDish.getDescription());
            dish.setPrice(updatedDish.getPrice());
            dish.setImageUrl(updatedDish.getImageUrl());
            dish.setCategory(updatedDish.getCategory());
            dish.setRestaurant(updatedDish.getRestaurant());
            return dishRepository.save(dish);
        }).orElseThrow(() -> new RuntimeException("Dish not found with id " + id));
    }

    // Xóa món ăn
    public void deleteDish(Long id) {
        dishRepository.deleteById(Math.toIntExact(id));
    }
}