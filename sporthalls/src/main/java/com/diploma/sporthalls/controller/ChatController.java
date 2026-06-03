package com.diploma.sporthalls.controller;


import com.diploma.sporthalls.model.ChatMessage;
import com.diploma.sporthalls.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Инструмент за изпращане на съобщения през WebSocket

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // 1. WebSocket обработчик: Когато Android прати съобщение към /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        // Записваме съобщението в базата данни за история
        chatMessageRepository.save(message);

        // Рутираме съобщението в реално време към всички абонирани за този конкретен чат
        // Android ще слуша на адрес: /topic/reservation/{reservationId}
        messagingTemplate.convertAndSend("/topic/reservation/" + message.getSportsHallId(), message);
    }

    // 2. HTTP Ендпоинт: Зареждане на историята при отваряне на чат прозореца в Android
    @GetMapping("/api/v1/chat/history/{reservationId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long reservationId) {
        List<ChatMessage> history = chatMessageRepository.findBySportsHallIdOrderByTimestampAsc(reservationId);
        return ResponseEntity.ok(history);
    }
}
