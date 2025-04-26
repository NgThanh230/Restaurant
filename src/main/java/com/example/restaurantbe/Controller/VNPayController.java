package com.example.restaurantbe.Controller;

import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final VnPayService vnPayService;
    @Autowired
    public VNPayController(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestParam Long orderId) throws UnsupportedEncodingException {
        String paymentUrl = vnPayService.createVnPayPaymentUrl(orderId);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnPayReturn(@RequestParam Map<String, String> params) {
        return vnPayService.vnPayReturn(params);
    }
}
