package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.Repository.ReservationRepository;
import com.example.restaurantbe.sercurity.VnPayUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
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
    private ReservationRepository reservationRepository;
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;


    public String createVnPayPaymentUrl(String txnRef, String orderInfo, BigDecimal amount) throws UnsupportedEncodingException {
        long vnpAmount = amount.multiply(BigDecimal.valueOf(100)).longValue(); // Chuyển số tiền sang VND (đơn vị là đồng)

        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String orderType = "other";
        String locale = "vn";
        String currCode = "VND";
        String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);  // Thêm mã merchant của bạn
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo); // Mô tả đơn hàng
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", locale);
        vnpParams.put("vnp_ReturnUrl", "myapp://vnpay-return");  // Deeplink cho Flutter
        vnpParams.put("vnp_IpAddr", "127.0.0.1");  // Địa chỉ IP localhost (có thể thay bằng IP thật)
        vnpParams.put("vnp_CreateDate", createDate);

        String queryUrl = VnPayUtils.generateQueryString(vnpParams);
        String sign = VnPayUtils.hmacSHA512(vnp_HashSecret, queryUrl);  // Tạo chữ ký bảo mật

        return vnp_PayUrl + "?" + queryUrl + "&vnp_SecureHash=" + sign;  // Trả về URL thanh toán
    }

    public ResponseEntity<String> handleVnPayCallback(Map<String, String> params) {
        try {
            String vnpSecureHash = params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            String signData = VnPayUtils.generateQueryString(params);
            String calculatedHash = VnPayUtils.hmacSHA512(vnp_HashSecret, signData);

            if (!vnpSecureHash.equalsIgnoreCase(calculatedHash)) {
                return createAutoClosePage("Chữ ký không hợp lệ.");
            }

            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");

            if ("00".equals(responseCode)) {
                if (txnRef.startsWith("ORDER_")) {
                    Long orderId = Long.parseLong(txnRef.replace("ORDER_", ""));
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

                    order.setOrderStatus(Order.OrderStatus.Completed);
                    order.setPaymentMethod(Order.PaymentMethod.VNPAY);
                    order.setPaidAt(LocalDateTime.now());
                    orderRepository.save(order);
                }

                return createAutoClosePage("Thanh toán thành công!");
            } else {
                return createAutoClosePage("Thanh toán thất bại. Mã lỗi: " + responseCode);
            }
        } catch (Exception e) {
            return createAutoClosePage("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private ResponseEntity<String> createAutoClosePage(String message) {
        String html = "<html><body>" +
                "<p style='text-align:center;margin-top:40px;font-size:18px;'>" + message + "</p>" +
                "<script>setTimeout(() => { window.close(); }, 1500);</script>" +
                "</body></html>";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }



}
