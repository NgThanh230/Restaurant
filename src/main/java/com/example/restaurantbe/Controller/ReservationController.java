package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.ReservationRequestDto;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
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
    //hủy đặt bàn
    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        Reservation updatedReservation = reservationService.updateReservationStatus(id, status);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }
    //xác nhận đặt bàn
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Integer id) {
        Reservation updatedReservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(updatedReservation);
    }
    @PatchMapping("/{id}/check-in")
    public ResponseEntity<Reservation> checkInCustomer(@PathVariable Integer id) {
        try {
            Reservation updatedReservation = reservationService.checkInCustomer(id);
            return ResponseEntity.ok(updatedReservation);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
