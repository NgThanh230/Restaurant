package com.example.restaurantbe.DTO;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private GetUserLogin user;

    public LoginResponse(String token, GetUserLogin user) {
        this.token = token;
        this.user = user;
    }
}