package com.example.manageruser.Dto;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ProfileResponse {
    private User user;
    private List<User> friendlists;
    private List<PostDTO> userPosts; // Chuyển sang Page<Post> để giữ toàn bộ thông tin phân trang
    private int currentPage;
    private int totalPages;

    // Constructor

    public ProfileResponse(User user, int totalPages, int currentPage, List<PostDTO> userPosts, List<User> friends) {
        this.user = user;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.userPosts = userPosts;
        this.friendlists = friendlists;
    }
}
