package com.example.restaurantbe.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationResponseDto {
    private Integer reservationId;
    private TableDto table;
    private LocalDateTime reservationDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    @Data
    public static class TableDto {
        private Integer id;
        private String name;
        private Integer capacity;
    }
}