package com.example.manageruser.Dto;

import com.example.manageruser.Model.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserWithLastMessageDTO {
    private String username;
    private String lastMessageContent;
    private Long chatId;
    private Long userId; // Added field for userId
    private LocalDateTime lastMessageTimestamp; // Field for last message timestamp

    public UserWithLastMessageDTO(String username, LocalDateTime lastMessageTimestamp, Long userId, Long chatId, String lastMessageContent) {
        this.username = username;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.userId = userId; // Initialize userId
        this.chatId = chatId;
        this.lastMessageContent = lastMessageContent;
    }
}
