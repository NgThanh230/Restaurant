package com.example.restaurantbe.DTO;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}