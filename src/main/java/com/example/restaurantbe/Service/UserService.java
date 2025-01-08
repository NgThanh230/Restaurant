package com.example.restaurantbe.Service;

import com.example.restaurantbe.Auth.JwtUtils;
import com.example.restaurantbe.DTO.UserLoginDto;
import com.example.restaurantbe.DTO.GetUserLogin;
import com.example.restaurantbe.DTO.LoginResponse;
import com.example.restaurantbe.DTO.UserRegisterDto;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.UserRepository;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final AuthenticationService authenticationService;

    public UserService(JwtUtils jwtUtils, AuthenticationService authenticationService) {
        this.jwtUtils = jwtUtils;
        this.authenticationService = authenticationService;
    }

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

    public LoginResponse login(UserLoginDto userLoginDto) throws BadRequestException {
        User authenticatedUser = authenticationService.authenticate(userLoginDto);

        if (!passwordEncoder.matches(userLoginDto.getPassword(), authenticatedUser.getPassword())) {
            throw new BadRequestException("Email hoặc mật khẩu không đúng.");
        }

        String jwt = jwtUtils.generateToken(authenticatedUser);
        GetUserLogin userLogin = new GetUserLogin(authenticatedUser);

        return new LoginResponse(jwt, userLogin);
    }
}
