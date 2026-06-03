package com.diploma.sporthalls.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Взимаме Authorization хедъра от заявката
        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 2. JWT токенът стандартно започва с текста "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Вместо твърдо substring(7), изрязваме "Bearer " по интелигентен начин и махаме излишни интервали в краищата
            jwt = authHeader.replace("Bearer ", "").trim();
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                logger.error("Невалиден или изтекъл JWT токен: " + e.getMessage());
            }
        }

        // 3. Ако сме намерили имейл и потребителят все още не е автентикиран в текущата сесия
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // За момента проверяваме дали токенът не е изтекъл спрямо имейла
            if (jwtUtil.validateToken(jwt, email)) {

                // Извличаме ролята, която запечатахме в токена (напр. "USER" или "OWNER")
                String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

                // Превръщаме ролята във формат, който Spring Security разбира (добавяме конвенционалния префикс ROLE_)
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // Създаваме обект за автентикация
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Поставяме потребителя в контекста на сигурността на Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
