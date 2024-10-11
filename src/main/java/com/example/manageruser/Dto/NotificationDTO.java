package com.example.manageruser.Dto;

public class NotificationDTO {

    private Long id;
    private String content;
    private String receiverUsername;

    // Constructor
    public NotificationDTO(Long id, String content, String receiverUsername) {
        this.id = id;
        this.content = content;
        this.receiverUsername = receiverUsername;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}
