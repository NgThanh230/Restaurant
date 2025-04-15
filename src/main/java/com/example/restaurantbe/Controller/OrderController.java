package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.OrderRequest;
import com.example.restaurantbe.DTO.OrderResponse;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
    // Lấy đơn hàng của user theo userId
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
//        List<Order> orders = orderService.getOrdersByUserId(userId);
//        if (orders.isEmpty()) {
//            return ResponseEntity.notFound().build();  // Nếu không tìm thấy đơn hàng
//        }
//        return ResponseEntity.ok(orders);  // Trả về danh sách đơn hàng của user
//    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
