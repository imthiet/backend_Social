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

    public boolean existsByUserIdAndPostId(long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
    public void save(Like like) {
        likeRepository.save(like);
    }

    public Long countLikesByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // LikeService
    public void deleteByUserIdAndPostId(long userId, Long postId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
    }

}

