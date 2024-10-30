package com.example.manageruser.Repository;

import com.example.manageruser.Model.Like;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);
}
