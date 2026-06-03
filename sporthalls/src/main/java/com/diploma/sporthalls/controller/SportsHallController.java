package com.diploma.sporthalls.controller;


import com.diploma.sporthalls.model.Reservation;
import com.diploma.sporthalls.model.SportsHall;
import com.diploma.sporthalls.model.User;
import com.diploma.sporthalls.repository.ReservationRepository;
import com.diploma.sporthalls.repository.SportsHallRepository;
import com.diploma.sporthalls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/halls")
@CrossOrigin(origins = "*")
public class SportsHallController {

    @Autowired
    private SportsHallRepository sportsHallRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    // Endpoint: GET http://localhost:8080/api/v1/halls
    // Връща всички одобрени спортни зали за Android приложението
    @GetMapping
    public ResponseEntity<List<SportsHall>> getAllApprovedHalls() {
        List<SportsHall> approvedHalls = sportsHallRepository.findByStatus("APPROVED");
        return ResponseEntity.ok(approvedHalls);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<SportsHall>> getMyHalls(Principal principal) {

        String ownerEmail = principal.getName();
        List<SportsHall> myHalls = sportsHallRepository.findByOwnerEmail(ownerEmail);
        return ResponseEntity.ok(myHalls);

    }


    // Ендпоинт: POST http://localhost:8080/api/v1/halls
    // Позволява на собственик да добави нова зала
    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SportsHall> createSportsHall(@RequestBody SportsHall sportsHall, Principal principal) {

        // Използваме вградения Principal, за да вземем имейла от JWT токена
        String ownerEmail = principal.getName();

        // Намираме потребителя в базата
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Потребителят не е намерен"));

        // Закачаме собственика към залата
        sportsHall.setOwner(owner);

        // Записваме директно през репозиторито
        SportsHall savedHall = sportsHallRepository.save(sportsHall);
        return ResponseEntity.ok(savedHall);
    }

    // 6. API за заети часове: GET http://localhost:8080/api/v1/halls/{id}/reservations?date=2026-06-02
    @GetMapping("/{id}/reservations")
    public ResponseEntity<?> getHallReservationsByDate(
            @PathVariable Long id,
            @RequestParam("date") String dateStr) { // Приема датата като String от Android (напр. "2026-06-02")

        try {
            // 1. Парсваме String датата към LocalDate
            java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr);

            // 2. Дефинираме границите на деня: от 00:00:00 до 23:59:59
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(java.time.LocalTime.MAX);

            // 3. Дърпаме списъка от базата данни
            List<Reservation> reservations = reservationRepository.findReservationsByHallAndDate(id, startOfDay, endOfDay);

            return ResponseEntity.ok(reservations);

        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body("{\"message\": \"Невалиден формат на датата. Използвайте YYYY-MM-DD.\"}");
        }
    }

    // 2. ТЪРСЕНЕ НА СВОБОДНИ ЗАЛИ: GET http://localhost:8080/api/v1/halls/search?date=2026-06-02&time=18:00
    @GetMapping("/search")
    public ResponseEntity<List<SportsHall>> searchFreeHalls(
            @RequestParam("date") String dateStr, // "2026-06-02"
            @RequestParam("time") String timeStr) { // "18:00"

        // 1. Парсваме датата и часа, дошли от Android
        java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
        java.time.LocalTime time = java.time.LocalTime.parse(timeStr);

        LocalDateTime startSearch = date.atTime(time);
        LocalDateTime endSearch = startSearch.plusHours(1); // Приемаме стандартен 1 час сесия

        // 2. Взимаме ВСИЧКИ одобрени зали
        List<SportsHall> allApprovedHalls = sportsHallRepository.findByStatus("APPROVED");

        // 3. Филтрираме и оставяме само свободните
        List<SportsHall> freeHalls = new java.util.ArrayList<>();

        for (SportsHall hall : allApprovedHalls) {
            // Проверяваме дали залата има конфликтна резервация за това време
            boolean isBusy = reservationRepository.hasConflictingReservation(hall.getId(), startSearch, endSearch);

            // Проверяваме и дали часът изобщо попада в нейното работно време
            LocalTime open = java.time.LocalTime.parse(hall.getWorkingHoursFrom());
            LocalTime close = java.time.LocalTime.parse(hall.getWorkingHoursTo());
            boolean isWorking = !time.isBefore(open) && !time.isAfter(close.minusHours(1));

            if (!isBusy && isWorking) {
                freeHalls.add(hall); // Залата е свободна и работи, добавяме я в резултатите
            }
        }

        return ResponseEntity.ok(freeHalls);
    }
}
