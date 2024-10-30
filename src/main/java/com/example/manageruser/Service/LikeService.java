package com.example.manageruser.Service;

import com.example.manageruser.Model.Like;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public boolean existsByUserAndPost(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }

    public void save(Like like) {
        likeRepository.save(like);
    }
}

