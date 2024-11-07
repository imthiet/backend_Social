package com.example.manageruser.Dto;

import jakarta.persistence.Lob;

import java.sql.Blob;

public class CommentDTO {
    private String username;
    private String content;
    private String image; // Add image field


    public CommentDTO(String username, String content, String image) {
        this.username = username;
        this.content = content;
        this.image = image; // Initialize image
    }
    // Getter và Setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
