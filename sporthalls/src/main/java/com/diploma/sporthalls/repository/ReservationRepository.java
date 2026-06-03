package com.diploma.sporthalls.repository;


import com.diploma.sporthalls.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Списък с резервациите на конкретен клиент (за Android екрана "Моите резервации")
    List<Reservation> findByUserId(Long userId);

    // Списък с резервациите за конкретна спортна зала
    List<Reservation> findBySportsHallId(Long sportsHallId);

    // Заявка за проверка на конфликтни дублирания
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.sportsHall.id = :hallId " +
            "AND :newStart < r.endTime " +
            "AND :newEnd > r.startTime")
    boolean hasConflictingReservation(
            @Param("hallId") Long hallId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );

    // НОВО: Взимане на всички резервации за конкретна зала в определен времеви диапазон (за деня)
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.sportsHall.id = :hallId " +
            "AND r.startTime >= :startOfDay " +
            "AND r.endTime <= :endOfDay")
    List<Reservation> findReservationsByHallAndDate(
            @Param("hallId") Long hallId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // Вече го имаш от стъпка 5, просто ще го извикаме и тук
    boolean existsBySportsHallIdAndEndTimeAfterAndStartTimeBefore(Long hallId, LocalDateTime newEnd, LocalDateTime newStart);

}
