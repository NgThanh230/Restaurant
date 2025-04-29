package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.RestaurantTable;
import com.example.restaurantbe.Repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantTableService {

    @Autowired
    private RestaurantTableRepository tableRepository;

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable getTableById(Integer id) {
        return tableRepository.findById(Long.valueOf(id)).orElse(null);
    }

    public RestaurantTable saveTable(RestaurantTable table) {
        return tableRepository.save(table);
    }
    public List<RestaurantTable> getAvailableTables() {
        return tableRepository.findByStatus("Available");
    }
    public void deleteTable(Integer id) {
        tableRepository.deleteById(Long.valueOf(id));
    }
}
