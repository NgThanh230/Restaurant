package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.OrderItemRequest;
import com.example.restaurantbe.DTO.OrderRequest;
import com.example.restaurantbe.DTO.OrderResponse;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.RestaurantTable;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.DishRepository;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Repository.RestaurantTableRepository;
import com.example.restaurantbe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final DishRepository dishRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService, DishRepository dishRepository, RestaurantTableRepository restaurantTableRepository) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.dishRepository = dishRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }
    public Order createOrder(OrderRequest orderRequest) {
        RestaurantTable table = restaurantTableRepository.findById(orderRequest.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));
        // Tạo đối tượng Order
        Order order = new Order();
        order.setTable(table);
        order.setNote(orderRequest.getNote());
        order.setOrderStatus(Order.OrderStatus.Pending);
        order.setCreatedAt(LocalDateTime.now());
        // Tính tổng giá trị đơn hàng
        BigDecimal totalPrice = calculateTotalPrice(orderRequest.getItems());
        order.setTotalPrice(totalPrice);
        // Lưu Order vào database
        orderRepository.save(order);
        // Lưu danh sách OrderItem
        orderItemService.saveOrderItems(order, orderRequest.getItems());

        return order;
    }

    public BigDecimal calculateTotalPrice(List<OrderItemRequest> items) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest item : items) {
            Dish dish = dishRepository.findById(item.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));
            totalPrice = totalPrice.add(dish.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return totalPrice;
    }


    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderResponse(order.getOrderId(), order.getTable(),
                order.getTotalPrice(),order.getNote(), order.getOrderStatus().toString(),
                order.getCreatedAt());
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }
}


