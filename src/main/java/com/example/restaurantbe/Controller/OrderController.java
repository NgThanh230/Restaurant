package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.OrderBillResponse;
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

    @GetMapping("/bills")
    public ResponseEntity<List<OrderBillResponse>> getAllOrderBills() {
        return ResponseEntity.ok(orderService.getAllOrderBills());
    }

    @GetMapping("/{id}/bill")
    public ResponseEntity<OrderBillResponse> getOrderBill(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderBill(id));
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

    @PostMapping("/{id}/pay-cash")
    public ResponseEntity<?> payCash(@PathVariable Long id) {
        // Tìm đơn hàng trong cơ sở dữ liệu
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại"));

        // Kiểm tra nếu đơn hàng đã được thanh toán hoặc đã bị hủy
        if (order.getOrderStatus() != Order.OrderStatus.Pending) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Order đã được thanh toán hoặc đã bị hủy.",
                    "orderId", order.getOrderId()
            ));
        }

        // Cập nhật thông tin thanh toán tiền mặt
        order.setPaymentMethod(Order.PaymentMethod.CASH);
        order.setOrderStatus(Order.OrderStatus.Completed);
        order.setPaidAt(LocalDateTime.now());

        // Lưu lại trạng thái đơn hàng sau khi cập nhật
        orderRepository.save(order);

        // Trả về kết quả sau khi thanh toán
        return ResponseEntity.ok(Map.of(
                "message", "Thanh toán tiền mặt thành công.",
                "orderId", order.getOrderId(),
                "totalPrice", order.getTotalPrice(),
                "paidAt", order.getPaidAt()
        ));
    }


}
