package com.example.restaurantbe.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationRequestDto {
    private Long userId;
    private String guestName;
    private String guestPhone;
    private Integer numberOfPeople;
    private LocalDateTime startTime;
    private String notes;
}
