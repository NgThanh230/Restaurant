package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.OrderItemRequest;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.OrderItem;
import com.example.restaurantbe.Repository.DishRepository;
import com.example.restaurantbe.Repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, DishRepository dishRepository) {
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
    }

    public void saveOrderItems(Order order, List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            Dish dish = dishRepository.findById(item.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));
            BigDecimal itemPrice = dish.getPrice().multiply(new BigDecimal(item.getQuantity()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDish(dish);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(itemPrice);
            orderItemRepository.save(orderItem);
        }
    }
}