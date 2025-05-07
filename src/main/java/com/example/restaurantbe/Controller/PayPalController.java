package com.example.restaurantbe.Controller;

import com.example.restaurantbe.Entity.Order;
import com.example.restaurantbe.Repository.OrderRepository;
import com.example.restaurantbe.sercurity.PayPalConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final PayPalConfig payPalConfig;
    private final OrderRepository orderRepository;

    @Value("${paypal.return-url}")
    private String returnUrl;

    @Value("${paypal.cancel-url}")
    private String cancelUrl;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrderFromDb(@RequestParam Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại với ID: " + orderId));
        if (order.getOrderStatus() == Order.OrderStatus.Completed) {
            throw new IllegalStateException("Order này đã được thanh toán");
        }

        String accessToken = payPalConfig.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Format số tiền đúng chuẩn PayPal
        String formattedPrice = order.getTotalPrice().setScale(2, RoundingMode.HALF_UP).toString();

        // Payload gửi cho PayPal
        Map<String, Object> orderData = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of(
                                "currency_code", "USD",
                                "value", formattedPrice
                        )
                )),
                "application_context", Map.of(
                        "return_url", returnUrl + "?orderId=" + order.getOrderId(),
                        "cancel_url", cancelUrl + "?orderId=" + order.getOrderId()
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderData, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api-m.sandbox.paypal.com/v2/checkout/orders", request, Map.class
        );

        List<Map<String, String>> links = (List<Map<String, String>>) response.getBody().get("links");
        String approvalUrl = links.stream()
                .filter(link -> "approve".equals(link.get("rel")))
                .findFirst()
                .map(link -> link.get("href"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy link approve từ PayPal"));

        // Trả lại URL để frontend redirect người dùng đến PayPal
        return ResponseEntity.ok(Map.of(
                "approvalUrl", approvalUrl,
                "paypalOrderId", response.getBody().get("id"),
                "localOrderId", order.getOrderId()
        ));
    }


    @GetMapping("/success")
    public void capturePayment(@RequestParam("token") String paypalOrderId,
                               @RequestParam("orderId") Long localOrderId,
                               HttpServletResponse response) throws IOException {
        String accessToken = payPalConfig.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Gọi PayPal để capture đơn hàng
            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                    "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + paypalOrderId + "/capture",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map responseBody = apiResponse.getBody();

            if (responseBody == null || !responseBody.containsKey("status")) {
                throw new RuntimeException("Không nhận được trạng thái từ PayPal");
            }

            String status = (String) responseBody.get("status");

            if ("COMPLETED".equalsIgnoreCase(status)) {
                // Cập nhật Order trong DB
                Order order = orderRepository.findById(localOrderId)
                        .orElseThrow(() -> new RuntimeException("Order không tồn tại"));

                order.setPaymentMethod(Order.PaymentMethod.PAYPAL);
                order.setOrderStatus(Order.OrderStatus.Completed);
                order.setPaidAt(LocalDateTime.now());
                orderRepository.save(order);
            }

            // Gửi HTML trả về với JavaScript để đóng trang
            response.setContentType("text/html");
            response.getWriter().write("<html><body><script type='text/javascript'>window.close();</script></body></html>");
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            // Nếu có lỗi, chỉ cần trả về mã lỗi và đóng trang
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<html><body><script type='text/javascript'>window.close();</script></body></html>");
        }
    }




    @GetMapping("/paypal/cancel")
    public void paypalCancel(@RequestParam Long orderId, HttpServletResponse response) throws IOException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Trả về trang thông báo hủy thanh toán
        String html = "<html><body><script>"
                + "window.opener.postMessage('payment_cancel', '*');"
                + "window.close();"
                + "</script></body></html>";

        response.setContentType("text/html");
        response.getWriter().write(html);
    }

}
