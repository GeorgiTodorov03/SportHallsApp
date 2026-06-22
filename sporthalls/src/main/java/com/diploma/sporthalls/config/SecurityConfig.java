package com.diploma.sporthalls.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("*")); // Позволява достъп от всякъде (вкл. емулатора)
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    return corsConfiguration;
                }))
                // 1. Изключваме CSRF защита, защото за REST API с JWT токени тя не е необходима
                .csrf(csrf -> csrf.disable())
                // 2. Настройваме кои пътища са публични и кои заключени
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/halls/**").permitAll() // Залите искат токен (работи)
                        .requestMatchers("/api/v1/reservations/**").authenticated() // ВАЖНО: Добави това, за да кажеш на Spring, че този URL също очаква JWT токен!// Всичко под /auth е достъпно за всеки
                        .requestMatchers("/api/v1/auth/**", "/ws/**").permitAll()
                        .anyRequest().authenticated())                         // Всички останали заявки изискват токен

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Грешка: Трябва да сте логнат!");
                        })
                )
        // 3. Казваме на Spring да не поддържа сесии (Stateless), тъй като сме с JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. Добавяме нашия JwtFilter в брънката от филтри, ПРЕДИ стандартния филтър за потребителско име и парола
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 5. Дефинираме BCryptPasswordEncoder като Bean, за да можем да го ползваме в AuthController
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
