package com.diploma.sporthalls.repository;


import com.diploma.sporthalls.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Връща потребител по неговия имейл.
    // Използваме Optional, защото потребителят може и да не съществува.
    Optional<User> findByEmail(String email);

    // Проверка дали имейлът вече е зает при регистрация
    boolean existsByEmail(String email);
}
