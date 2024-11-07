package com.example.manageruser.Repository;

import com.example.manageruser.Model.Like;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(long userId, Long postId); // Sử dụng ID thay vì thực thể


    Long countByPostId(Long postId);

     @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    void deleteByUserIdAndPostId(long userId, Long postId);

}
