package com.example.restaurantbe.Repository;

import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
 List<Reservation> findByUserName(String userName);
 List<Reservation> findByTable_TableNumber(String tableNumber);

 List<Reservation> findByStatusAndReservationDateBefore(String status, LocalDateTime time);
 @Query("""
    SELECT COUNT(r) > 0 FROM Reservation r
    WHERE r.table.tableId = :tableId
      AND (:startTime < r.endTime AND :endTime > r.startTime)
""")
 boolean isTimeConflict(@Param("tableId") Long tableId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

}
