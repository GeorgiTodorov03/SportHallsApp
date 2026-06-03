package com.diploma.sporthalls.repository;


import com.diploma.sporthalls.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Взимане на историята на чата за конкретна резервация, подредена по време
    List<ChatMessage> findBySportsHallIdOrderByTimestampAsc(Long sportsHallId);

}
