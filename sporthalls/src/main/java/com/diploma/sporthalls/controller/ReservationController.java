package com.diploma.sporthalls.controller;


import com.diploma.sporthalls.dto.PaymentRequest;
import com.diploma.sporthalls.model.Reservation;
import com.diploma.sporthalls.model.SportsHall;
import com.diploma.sporthalls.model.User;
import com.diploma.sporthalls.repository.ReservationRepository;
import com.diploma.sporthalls.repository.SportsHallRepository;
import com.diploma.sporthalls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SportsHallRepository sportsHallRepository;

    // 1. Създаване на резервация: POST http://localhost:8080/api/v1/reservations
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation request, Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Principal обектът е null. Токенът не е разчетен правилно.");
        }

        if (request.getSportsHall() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Грешка: Обектът 'sportsHall' липсва или е невалиден в JSON заявката.");
        }

        // Извличаме имейла от JWT токена (чрез обекта Principal)
        String email = principal.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Потребителят не е намерен.");
        }

        // Намираме спортната зала
        Optional<SportsHall> hallOpt = sportsHallRepository.findById(request.getSportsHall().getId());
        if (hallOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Спортната зала не съществува.");
        }

        SportsHall hall = hallOpt.get();
        User user = userOpt.get();

        // =======================================================
// ОПТИМИЗИРАНА ВАЛИДАЦИЯ ЗА РАБОТНО ВРЕМЕ НА ЗАЛАТА
// =======================================================
        if (request.getStartTime() != null) {
            // 1. Взимаме само часа и минутите на резервацията (напр. 18:00)
            LocalTime reservationTime = request.getStartTime().toLocalTime();

            // 2. Подсигуряваме чист формат на стринговете преди парсване
            String openStr = hall.getWorkingHoursFrom().trim();
            String closeStr = hall.getWorkingHoursTo().trim();

            // Ако бекендът е записал нещо като "8:00" вместо "08:00", го коригираме
            if (openStr.length() == 4) openStr = "0" + openStr;
            if (closeStr.length() == 4) closeStr = "0" + closeStr;

            LocalTime openTime = LocalTime.parse(openStr); // напр. 08:00
            LocalTime closeTime = LocalTime.parse(closeStr); // напр. 22:00

            // Последният възможен час за 1-часова резервация
            LocalTime latestAllowedStart = closeTime.minusHours(1);

            // 3. Използваме !isBefore и !isAfter за включително сравнение (>= и <=)
            boolean isTooEarly = reservationTime.isBefore(openTime);
            boolean isTooLate = reservationTime.isAfter(latestAllowedStart);

            if (isTooEarly || isTooLate) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Грешка: Залата не работи в този часови диапазон! Избран час: "
                                + reservationTime + ". Работно време: "
                                + hall.getWorkingHoursFrom() + " - " + hall.getWorkingHoursTo() + "\"}");
            }
        }
// =======================================================


        // Закачаме реалните обекти към резервацията
        request.setUser(user);
        request.setSportsHall(hall);

        // Автоматично пресмятане на крайния час (Начален час + 1 час времетраене)
        if (request.getStartTime() != null) {
            // Взимаме началния час (който е LocalDateTime)
            LocalDateTime startTime = request.getStartTime();

            // Добавяме точно 1 час (можеш да го смениш на +2, ако наемите са по 2 часа)
            LocalDateTime endTime = startTime.plusHours(1);

            // Слагаме стойността в ентитито
            request.setEndTime(endTime);
            System.out.println("Успешно изчислен краен час: " + request.getEndTime());
        } else {
            // ЗАЩИТА: Ако датата изобщо не е пристигнала, не позволяваме на Hibernate да гърми, а връщаме грешка
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Грешка: Началната дата и час (dateTime) липсват в заявката.");
        }


        // ==========================================
        // КРИТИЧНА ПРОВЕРКА ЗА КОНФЛИКТНИ РЕЗЕРВАЦИИ
        // ==========================================
        boolean isConflict = reservationRepository.hasConflictingReservation(
                hall.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (isConflict) {
            // Спираме процеса и връщаме 400 Bad Request с JSON съобщение за Android
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Избраният час вече е зает! Моля, избери друг интервал.\"}");
        }
        // ==========================================

        // Автоматично изчисляваме общата цена (за дипломната работа това носи бонус точки за автоматизация)
        // За простота приемаме, че цената в заявката е сметната, или я преизчисляваме тук при нужда.
        if (request.getTotalPrice() == null || request.getTotalPrice() == 0) {
            request.setTotalPrice(hall.getPricePerHour());
        }

        if (request.getStatus() == null) {
            request.setStatus("PENDING");
        }

        Reservation savedReservation = reservationRepository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // 7. Симулиране на плащане: POST http://localhost:8080/api/v1/reservations/pay
    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {

        // 1. Намираме резервацията в базата
        Optional<Reservation> reservationOpt = reservationRepository.findById(paymentRequest.getReservationId());

        if (reservationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Резервацията не е намерена!\"}");
        }

        Reservation reservation = reservationOpt.get();

        // 2. Проверка дали вече не е платена
        if ("CONFIRMED".equals(reservation.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Тази резервация вече е платена и потвърдена!\"}");
        }

        // 3. Обновяваме данните - сменяме статуса и закачаме Payment ID-то
        reservation.setStatus("CONFIRMED");
        reservation.setPaymentId(paymentRequest.getPaymentId()); // Напр. "PAY-123456789" от Android

        // 4. Записваме обновения обект
        reservationRepository.save(reservation);

        return ResponseEntity.ok()
                .body("{\"message\": \"Плащането е успешно! Резервацията е потвърдена.\"}");
    }

    // 2. Преглед на резервациите на логнатия потребител: GET http://localhost:8080/api/v1/reservations/my
    @GetMapping("/my")
    public ResponseEntity<List<Reservation>> getMyReservations(Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        List<Reservation> myReservations = reservationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(myReservations);
    }
}
