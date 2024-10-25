package com.example.manageruser.Dto;

import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.NotificationType;
import java.time.LocalDateTime;

public class NotificationDTO {
    private String contentnoti;
    private LocalDateTime timestamp;
    private String status;
    private NotificationType type;
    private String sender_username; // Trường mới để chứa tên người gửi

    // Constructor nhận Notification làm tham số
    public NotificationDTO(Notification notification) {
        this.contentnoti = notification.getContentnoti();
        this.type = notification.getType();
        this.status = notification.getStatus();
        this.timestamp = notification.getTimestamp();
        this.sender_username = notification.getSender() != null ? notification.getSender().getUsername() : null;
    }

    // Getters và Setters
    public String getContentnoti() {
        return contentnoti;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setContentnoti(String contentnoti) {
        this.contentnoti = contentnoti;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
