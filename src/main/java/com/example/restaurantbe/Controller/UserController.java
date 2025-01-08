package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.UserLoginDto;
import com.example.restaurantbe.DTO.UserRegisterDto;
import com.example.restaurantbe.Service.UserService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginDto userLoginDto) throws BadRequestException {
        return userService.login(userLoginDto);
    }
}
