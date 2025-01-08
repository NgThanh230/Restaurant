package com.example.restaurantbe.DTO;

import java.time.LocalDateTime;

import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Entity.User.Role;

import lombok.Data;

@Data
public class GetUserLogin {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;

    public GetUserLogin(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.createdAt = user.getCreatedAt();
    }
}
