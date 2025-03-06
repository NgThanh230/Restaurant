package com.example.restaurantbe.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationRequestDto {
    private Integer userId;
    private Integer tableId;
    private LocalDateTime reservationDate;
    private String status;
    private String notes;
}