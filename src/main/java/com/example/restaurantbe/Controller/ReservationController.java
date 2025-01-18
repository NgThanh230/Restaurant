package com.example.restaurantbe.Controller;

import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.saveReservation(reservation);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Integer id) {
        return reservationService.getReservationById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Integer id) {
        reservationService.deleteReservation(id);
    }
    @GetMapping("/byCustomer/{customerName}")
    public List<Reservation> getReservationsByCustomerName(@PathVariable String customerName) {
        return reservationService.getReservationsByCustomerName(customerName);
    }
    @GetMapping("/byTable/{tableNumber}")
    public List<Reservation> getReservationsByTableNumber(@PathVariable String tableNumber) {
        return reservationService.getReservationsByTableNumber(tableNumber);
    }
}
