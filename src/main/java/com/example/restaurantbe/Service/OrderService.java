package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.*;
import com.example.restaurantbe.Entity.Dish;
import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.RestaurantTable;


import com.example.restaurantbe.Repository.DishRepository;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Repository.RestaurantTableRepository;
import com.example.restaurantbe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Tạo đối tượng Order
        Order order = new Order();
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
    public OrderBillResponse getOrderBill(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy order"));

        // Chuyển thông tin món ăn
        List<OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setDishName(item.getDish().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return dto;
        }).collect(Collectors.toList());

        // Tính tổng tiền
        BigDecimal totalPrice = items.stream()
                .map(OrderItemDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo response
        OrderBillResponse response = new OrderBillResponse();
        response.setOrderId(order.getOrderId());
        response.setTotalPrice(totalPrice);
        response.setNote(order.getNote());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setPaymentMethod(String.valueOf(order.getPaymentMethod()));
        response.setCreatedAt(order.getCreatedAt());
        response.setPaidAt(order.getPaidAt());
        response.setItems(items);

        return response;
    }

    public List<OrderBillResponse> getAllOrderBills() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.Completed)
                .sorted(Comparator.comparing(Order::getPaidAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        return orders.stream().map(order -> {
            List<OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setDishName(item.getDish().getName());
                dto.setQuantity(item.getQuantity());
                dto.setPrice(item.getPrice());
                dto.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                return dto;
            }).collect(Collectors.toList());

            BigDecimal totalPrice = items.stream()
                    .map(OrderItemDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            OrderBillResponse response = new OrderBillResponse();
            response.setOrderId(order.getOrderId());
            response.setTotalPrice(totalPrice);
            response.setNote(order.getNote());
            response.setOrderStatus(order.getOrderStatus().name());
            response.setPaymentMethod(String.valueOf(order.getPaymentMethod()));
            response.setCreatedAt(order.getCreatedAt());
            response.setPaidAt(order.getPaidAt());
            response.setItems(items);

            return response;
        }).collect(Collectors.toList());
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


