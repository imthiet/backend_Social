package com.example.manageruser.Dto;

import java.time.LocalDateTime;

public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private byte[] image; // Sử dụng byte[] để lưu trữ dữ liệu hình ảnh BLOB
    private String createdBy;
    private LocalDateTime createdAt;

    // Constructors
    public PostDTO() {}

    public PostDTO(Long id, String title, String content, byte[] image, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
