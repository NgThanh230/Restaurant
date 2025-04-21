package com.example.restaurantbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestaurantBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantBeApplication.class, args);
    }

}
