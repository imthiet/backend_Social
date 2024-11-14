package com.example.manageruser.Dto;

import com.example.manageruser.Model.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserWithLastMessageDTO {
    private String username;
    private String lastMessageContent;
    private Long chatId;

    private LocalDateTime lastMessageTimestamp; // Thêm trường thời gian


    // Constructor
    public UserWithLastMessageDTO(String username, String lastMessageContent, LocalDateTime lastMessageTimestamp, Long chatId) {
        this.username = username;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.chatId = chatId;

    }
}
