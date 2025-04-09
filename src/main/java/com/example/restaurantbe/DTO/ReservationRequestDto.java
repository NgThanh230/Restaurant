package com.example.restaurantbe.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationRequestDto {
    private Long userId; // có thể null nếu khách vãng lai
    private Integer tableId;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private LocalDateTime startTime;
    private Integer durationMinutes; // Thời lượng đặt bàn, ví dụ 90 phút
    private String status;
    private String notes;

}