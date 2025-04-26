package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.sercurity.VnPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VnPayService {
    @Autowired
    private OrderRepository orderRepository;
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    public VnPayService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String createVnPayPaymentUrl(Long orderId) throws UnsupportedEncodingException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không Tìm Thấy Order"));
        if (order.getOrderStatus() == Order.OrderStatus.Completed) {
            throw new IllegalStateException("Order Này Đã Được Thanh Toán");
        }
        long amount = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();

        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String orderType = "other";
        String locale = "vn";
        String currCode = "VND";

        String txnRef = String.valueOf(order.getOrderId());
        String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Payment for order: " + order.getOrderId());
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", locale);
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1"); // test localhost
        vnpParams.put("vnp_CreateDate", createDate);

        String queryUrl = VnPayUtils.generateQueryString(vnpParams);
        String sign = VnPayUtils.hmacSHA512(vnp_HashSecret, queryUrl);

        return vnp_PayUrl + "?" + queryUrl + "&vnp_SecureHash=" + sign;
    }

    public ResponseEntity<?> vnPayReturn(@RequestParam Map<String, String> params) {
        try {
            // Lấy chữ ký và loại bỏ khỏi map để tính lại
            String vnpSecureHash = params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            // Tạo chuỗi ký để tính lại chữ ký
            String signData = VnPayUtils.generateQueryString(params);
            String calculatedHash = VnPayUtils.hmacSHA512(vnp_HashSecret, signData);

            if (!vnpSecureHash.equalsIgnoreCase(calculatedHash)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Thanh toán thất bại!",
                        "error", "Chữ ký không hợp lệ"
                ));
            }

            String responseCode = params.get("vnp_ResponseCode");
            String orderId = params.get("vnp_TxnRef"); // Giả sử đây là orderId

            if ("00".equals(responseCode)) {
                // Thành công → cập nhật đơn hàng
                Order order = orderRepository.findById(Long.parseLong(orderId))
                        .orElseThrow(() -> new RuntimeException("Không Tìm Thấy Order"));
                order.setOrderStatus(Order.OrderStatus.Completed);
                orderRepository.save(order);

                return ResponseEntity.ok(Map.of(
                        "message", "Thanh toán thành công!",
                        "orderId", order.getOrderId(),
                        "totalPrice", order.getTotalPrice(),
                        "status", order.getOrderStatus()
                ));
            } else {
                // Thanh toán thất bại → có thể cập nhật trạng thái đơn hàng, trả về lỗi chi tiết
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Thanh toán thất bại!",
                        "orderId", orderId,
                        "error", "VNPay Response Code: " + responseCode
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Có lỗi xảy ra trong quá trình xử lý callback",
                    "error", e.getMessage()
            ));
        }
    }
}
