package com.diploma.sporthalls.controller;


import com.diploma.sporthalls.config.JwtUtil;
import com.diploma.sporthalls.dto.LoginRequest;
import com.diploma.sporthalls.dto.RegisterRequest;
import com.diploma.sporthalls.model.Role;
import com.diploma.sporthalls.model.User;
import com.diploma.sporthalls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Използваме енкодера

    @Autowired
    private JwtUtil jwtUtil; // Използваме JWT генератора

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            System.out.println("ГРЕШКА ВЪВ ВАЛИДАЦИЯТА: " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors());
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        System.out.println("ГРЕШКА ПРИ ЧЕТЕНЕ НА JSON (JACKSON): " + ex.getMessage());
        if (ex.getCause() != null) {
            System.out.println("ДЪЛБОКА ПРИЧИНА: " + ex.getCause().getMessage());
        }
        return ResponseEntity.badRequest().body("{\"error\": \"" + ex.getMessage() + "\"}");
    }

    // 1. Ендпоинт за Регистрация: POST http://localhost:8080/api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        // Проверка дали имейлът вече съществува
        if(userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Грешка: Имейлът е вече зает");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());

        // КРИПТИРАНЕ НА ПАРОЛАТА ПРЕДИ ЗАПИС
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        // ДИНАМИЧНО ЗАДАВАНЕ НА РОЛЯ
        if (request.isOwner()) {
            newUser.setRole(Role.OWNER); // или вашия Enum / Свързана таблица за роли
        } else {
            newUser.setRole(Role.USER);
        }

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Потребителят е регистриран");
    }

    // 2. Ендпоинт за Вход (Login): POST http://localhost:8080/api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if(userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Грешка:" , "Несъществуваш потребител"));
        }

        User user = userOpt.get();

        // Проверка на криптираната парола
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Грешка:" , " Грешна парола"));
        }

        // Генериране на JWT токен
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Връщаме токена в чист JSON формат към Android клиента
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }
}
