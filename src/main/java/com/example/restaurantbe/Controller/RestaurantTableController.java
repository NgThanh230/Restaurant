package com.example.restaurantbe.Controller;

import com.example.restaurantbe.Entity.RestaurantTable;
import com.example.restaurantbe.Service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    @Autowired
    private RestaurantTableService tableService;

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return tableService.getAllTables();
    }

    @PostMapping
    public RestaurantTable createTable(@RequestBody RestaurantTable table) {
        return tableService.saveTable(table);
    }

    @GetMapping("/{id}")
    public RestaurantTable getTableById(@PathVariable Integer id) {
        return tableService.getTableById(id);
    }
    @GetMapping("/status/available")
    public List<RestaurantTable> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Integer id) {
        tableService.deleteTable(id);
    }
}
