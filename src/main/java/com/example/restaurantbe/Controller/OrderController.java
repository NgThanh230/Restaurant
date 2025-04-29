package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.OrderRequest;
import com.example.restaurantbe.DTO.OrderResponse;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }
    @GetMapping
    public List<Order> getAllOrder() {
        return orderService.getAllOrder();
    }
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

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

    //thanh toán tiền mặt
    @PostMapping("/{id}/pay-cash")
    public ResponseEntity<?> payCash(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != Order.OrderStatus.Pending) {
            return ResponseEntity.badRequest().body("Order has already been paid or cancelled.");
        }

        order.setPaymentMethod(Order.PaymentMethod.CASH);
        order.setOrderStatus(Order.OrderStatus.Completed);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Thanh toán tiền mặt thành công",
                "orderId", order.getOrderId(),
                "totalPrice", order.getTotalPrice()
        ));
    }

}
