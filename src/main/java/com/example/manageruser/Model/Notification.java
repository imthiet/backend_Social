package com.example.manageruser.Model;

import com.example.manageruser.Model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentnoti;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private String status; // e.g., "unread", "read"

    private LocalDateTime timestamp; // to store the notification creation time

    @Enumerated(EnumType.STRING) // Sử dụng EnumType.STRING để lưu trữ giá trị enum
    private NotificationType type;

    // Getters and Setters
    // (giữ nguyên các phương thức getter và setter, và thêm cho type)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentnoti() {
        return contentnoti;
    }

    public void setContentnoti(String contentnoti) {
        this.contentnoti = contentnoti;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", contentnoti='" + contentnoti + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' + // thêm type vào chuỗi trả về
                '}';
    }
}
