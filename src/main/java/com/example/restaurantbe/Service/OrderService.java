package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.OrderItemRequest;
import com.example.restaurantbe.DTO.OrderRequest;
import com.example.restaurantbe.DTO.OrderResponse;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.DishRepository;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemService orderItemService;
    private final DishRepository dishRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, OrderItemService orderItemService, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemService = orderItemService;
        this.dishRepository = dishRepository;
    }

    public Order createOrder(OrderRequest orderRequest) {
        // Lấy thông tin người dùng
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo đối tượng Order
        Order order = new Order();
        order.setTableId(orderRequest.getTableId());
        order.setUser(user);
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
            Dish dish = dishRepository.findByDishId(item.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));
            totalPrice = totalPrice.add(dish.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return totalPrice;
    }
//    public List<Order> getOrdersByUserId(Long userId) {
//        return orderRepository.findByUser_UserId(userId);
//    }
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = order.getUser();
        return new OrderResponse(order.getOrderId(), order.getTableId(), user.getName(),
                order.getTotalPrice(), order.getOrderStatus().toString(),
                 order.getCreatedAt());
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }
}


