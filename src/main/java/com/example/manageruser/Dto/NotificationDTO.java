package com.example.manageruser.Dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String contentnoti;
    private LocalDateTime timestamp;
    private String status ;


    public NotificationDTO(String contentnoti, LocalDateTime timestamp, String status) {
        this.contentnoti = contentnoti;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getContentnoti() {
        return contentnoti;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public String getStatus() {
        return status;
    }




}
