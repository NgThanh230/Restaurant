package com.example.restaurantbe.Controller;

import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Repository.ReservationRepository;
import com.example.restaurantbe.Service.OrderService;
import com.example.restaurantbe.Service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
public class VNPayController {

    private final VnPayService vnPayService;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    @Autowired
    public VNPayController(VnPayService vnPayService, OrderService orderService, OrderRepository orderRepository, ReservationRepository reservationRepository) {
        this.vnPayService = vnPayService;
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
    }
    @PostMapping("/order/{orderId}")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId) throws UnsupportedEncodingException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getOrderStatus() == Order.OrderStatus.Completed) {
            throw new IllegalStateException("Đơn hàng đã được thanh toán");
        }

        String txnRef = "ORDER_" + order.getOrderId();
        String orderInfo = "Payment for Order #" + order.getOrderId();
        BigDecimal amount = order.getTotalPrice();

        String paymentUrl = vnPayService.createVnPayPaymentUrl(txnRef, orderInfo, amount);
        return ResponseEntity.ok(Map.of("url", paymentUrl));
    }
    @PostMapping("/vnpay/reservation/{reservationId}")
    public ResponseEntity<?> payReservationDeposit(@PathVariable Integer reservationId) throws UnsupportedEncodingException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));

        String txnRef = "RESERVATION_" + reservation.getId();
        String orderInfo = "Deposit for Reservation #" + reservation.getId();
        BigDecimal depositAmount = BigDecimal.valueOf(100_000);

        String paymentUrl = vnPayService.createVnPayPaymentUrl(txnRef, orderInfo, depositAmount);
        return ResponseEntity.ok(Map.of("url", paymentUrl));
    }


    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnPayReturn(@RequestParam Map<String, String> params) {
        return vnPayService.handleVnPayCallback(params);
    }


}
