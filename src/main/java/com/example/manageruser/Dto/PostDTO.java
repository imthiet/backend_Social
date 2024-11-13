package com.example.manageruser.Dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;

    private String content;
    private byte[] image;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean isLiked; // New field
    private long likesCount; // New field for likes count
    private Page<CommentDTO> comments; // New field for comments

    // Constructors
    public PostDTO() {}

    public PostDTO(Long id, String content, byte[] image, String createdBy,
                   LocalDateTime createdAt, long likesCount, Page<CommentDTO> comments,boolean isLiked) {
        this.id = id;

        this.content = content;
        this.image = image;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.comments = comments;
        this.isLiked = isLiked;
    }

    // Getters and setters

}
