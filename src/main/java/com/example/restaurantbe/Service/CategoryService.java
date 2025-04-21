package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.CategoryDTO;
import com.example.restaurantbe.Entity.Category;
import com.example.restaurantbe.Repository.CategoryRepository;
import com.example.restaurantbe.Repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category với ID: " + id));
    }

    public Category createCategory(CategoryDTO dto) {
        Category category = new Category(dto.getName());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryDTO dto) {
        Category existing = getCategoryById(id);
        existing.setName(dto.getName());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại"));

        // Kiểm tra xem category có đang được sử dụng trong Dish nào không
        if (dishRepository.existsByCategory(category)) {
            throw new RuntimeException("Không thể xóa category vì có món ăn đang sử dụng category này");
        }

        categoryRepository.deleteById(id);
    }
}
