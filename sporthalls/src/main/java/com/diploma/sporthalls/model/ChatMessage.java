package com.diploma.sporthalls.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sportsHallId; // Свързваме чата с конкретна резервация/зала
    private String senderEmail; // Кой праща съобщението

    @Column(columnDefinition = "TEXT")
    private String content; // Текстът на съобщението

    private LocalDateTime timestamp = LocalDateTime.now();

    public ChatMessage() {}

    // Гетери и сетери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSportsHallId() {
        return sportsHallId;
    }

    public void setSportsHallId(Long sportsHallId) {
        this.sportsHallId = sportsHallId;
    }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
