package com.example.restaurantbe.Service;


import com.example.restaurantbe.DTO.UserLoginDto;
import com.example.restaurantbe.DTO.UserRegisterDto;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public String register(UserRegisterDto userRegisterDto) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            return ("Email đã được sử dụng.");
        }

        // Tạo User mới
        User user = new User();
        user.setName(userRegisterDto.getName());
        user.setEmail(userRegisterDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRegisterDto.getPassword()));

        // Đặt role mặc định là CUSTOMER nếu không có role từ DTO
        String role = userRegisterDto.getRole() != null ? userRegisterDto.getRole().toUpperCase() : "Customer";
        user.setRole(User.Role.valueOf(role));

        user.setPhoneNumber(userRegisterDto.getPhoneNumber());
        user.setAddress(userRegisterDto.getAddress());

        userRepository.save(user);
        return "Đăng ký thành công!";
    }
    public Object login(UserLoginDto userLoginDto) {
        Optional<User> userOpt = userRepository.findByEmail(userLoginDto.getEmail());

        if (userOpt.isEmpty() ||
                !passwordEncoder.matches(userLoginDto.getPassword(), userOpt.get().getPasswordHash())) {
            return ("Email hoặc mật khẩu không đúng.");
        }

        return userOpt.get();
    }
}
