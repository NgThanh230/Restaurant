package com.example.restaurantbe.Repository;

import com.example.restaurantbe.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
 List<Reservation> findByUserName(String userName);
 List<Reservation> findByTable_TableNumber(String tableNumber);

 List<Reservation> findByStatusAndReservationDateBefore(String status, LocalDateTime time);}