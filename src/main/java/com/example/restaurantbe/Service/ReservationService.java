package com.example.restaurantbe.Service;

import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> getReservationsByCustomerName(String customerName) {
        return reservationRepository.findByUserName(customerName);
    }
    public List<Reservation> getReservationsByTableNumber(String tableNumber) {
        return reservationRepository.findByTable_TableNumber(tableNumber);
    }
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Integer id) {
        reservationRepository.deleteById(id);
    }
}
