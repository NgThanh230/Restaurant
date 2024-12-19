package com.example.restaurantbe.Repository;

import com.example.restaurantbe.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Bạn có thể thêm các phương thức tùy chỉnh nếu cần
    Optional<Category> findById(Long id);
}