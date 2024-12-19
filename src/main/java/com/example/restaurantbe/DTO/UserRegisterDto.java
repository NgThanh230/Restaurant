package com.example.restaurantbe.DTO;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String role; // CUSTOMER, RESTAURANT, ADMIN, DELIVERY.
}