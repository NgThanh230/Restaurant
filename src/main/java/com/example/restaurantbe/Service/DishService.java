package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.Category;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.CategoryRepository;
import com.example.restaurantbe.Repository.DishRepository;
import com.example.restaurantbe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả món ăn
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    // Lấy món ăn theo ID
    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findByDishId(id);
    }

    // Thêm món ăn mới
    public Dish createDish(String name, String description, BigDecimal price, String imageUrl, Long categoryId, Long restaurantId) {
        // Kiểm tra xem category có tồn tại không
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category Không Tồn Tại"));
        // Kiểm tra xem user (restaurant) có tồn tại không
        User restaurant = userRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant Không Tồn Tại"));
        // Tạo món ăn mới với category và restaurant đã tồn tại
        Dish dish = new Dish(name, description, price, imageUrl, category, restaurant);
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
        dishRepository.deleteById(id);
    }
}