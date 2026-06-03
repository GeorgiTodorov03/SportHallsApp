package com.diploma.sporthalls.repository;


import com.diploma.sporthalls.model.SportsHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsHallRepository extends JpaRepository<SportsHall, Long> {

    // Spring Data JPA автоматично генерира SQL заявка по името на метода!
    // Връща всички зали, които са одобрени от админа (за клиента)
    List<SportsHall> findByStatus(String status);

    // Извлича залите, филтрирани по имейла на свързания потребител (owner)
    List<SportsHall> findByOwnerEmail(String email);

    // Връща всички зали на конкретен собственик (за неговото табло)
    List<SportsHall> findByOwnerId(Long ownerId);

    // Търсене по тип спорт и град/адрес
    List<SportsHall> findBySportTypeAndAddressContainingIgnoreCaseAndIsApprovedTrue(String sportType, String address);

}
