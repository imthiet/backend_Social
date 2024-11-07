package com.example.manageruser.Repository;

import com.example.manageruser.Dto.CommentDTO;
import com.example.manageruser.Model.Comment;
import com.example.manageruser.Model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    Page<Comment> findByPostId(Long postId, Pageable pageable); // This method supports pagination
}
