package com.example.manageruser.Dto;

import com.example.manageruser.Model.Message;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long chatId;
    private LocalDateTime timestamp;
    private Long senderId;
    private Long receiverId;
    private String senderUsername; // Thêm trường này để lưu tên người gửi

    public MessageDTO(Long id, String content, Long chatId, LocalDateTime timestamp, Long senderId, Long receiverId, String senderUsername) {
        this.id = id;
        this.content = content;
        this.chatId = chatId;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderUsername = senderUsername; // Khởi tạo tên người gửi
    }

    // Phương thức chuyển đổi từ Message entity sang MessageDTO
    public static MessageDTO fromMessage(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getChat() != null ? message.getChat().getId() : null, // Kiểm tra null để tránh lỗi NullPointerException
                message.getTimestamp(),
                message.getSender() != null ? message.getSender().getId() : null, // Kiểm tra null
                message.getReceiver() != null ? message.getReceiver().getId() : null, // Kiểm tra null
                message.getSender() != null ? message.getSender().getUsername() : null // Lấy tên người gửi từ User
        );
    }

    // Getters and setters
}
