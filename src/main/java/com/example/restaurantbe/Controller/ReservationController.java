package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.ReservationRequestDto;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequestDto requestDto) {
        Reservation createdReservation = reservationService.createReservation(requestDto);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Integer id) {
        Reservation reservation = reservationService.getReservationById(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Integer id) {
        reservationService.cancelReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        Reservation updatedReservation = reservationService.updateReservationStatus(id, status);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }
    /*
    @GetMapping("/byCustomer/{customerName}")
    public List<Reservation> getReservationsByCustomerName(@PathVariable String customerName) {
        return reservationService.getReservationsByCustomerName(customerName);
    }
    @GetMapping("/byTable/{tableNumber}")
    public List<Reservation> getReservationsByTableNumber(@PathVariable String tableNumber) {
        return reservationService.getReservationsByTableNumber(tableNumber);
    }

     */
}
