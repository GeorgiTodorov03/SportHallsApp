package com.diploma.sporthalls.controller;


import com.diploma.sporthalls.model.SportsHall;
import com.diploma.sporthalls.repository.SportsHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')") // Заключва целия контролер само за АДМИН
public class AdminController {

    @Autowired
    private SportsHallRepository sportsHallRepository;

    // 1. Вземане на всички зали, които чакат одобрение
    @GetMapping("/pending-halls")
    public ResponseEntity<List<SportsHall>> getPendingHalls() {
        List<SportsHall> pendingHalls = sportsHallRepository.findByStatus("PENDING");
        return ResponseEntity.ok(pendingHalls);
    }

    // 2. Одобряване на зала по ID
    @PutMapping("/halls/{id}/approve")
    public ResponseEntity<?> approveHall(@PathVariable Long id) {
        SportsHall hall = sportsHallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Залата не е намерена"));

        hall.setStatus("APPROVED");
        sportsHallRepository.save(hall);

        return ResponseEntity.ok().body("{\"message\": \"Залата е одобрена успешно!\"}");
    }

    // 3. Отхвърляне / Изтриване на зала по ID
    @DeleteMapping("/halls/{id}")
    public ResponseEntity<?> deleteOrRejectHall(@PathVariable Long id) {
        SportsHall hall = sportsHallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Залата не е намерена"));

        // Може директно да я изтрием от базата, ако е отхвърлена:
        sportsHallRepository.delete(hall);

        return ResponseEntity.ok().body("{\"message\": \"Залата е премахната/отхвърлена!\"}");
    }
}
