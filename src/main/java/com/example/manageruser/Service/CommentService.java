package com.example.manageruser.Service;


import com.example.manageruser.Dto.CommentDTO;
import com.example.manageruser.Model.Comment;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.CommentRepository;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    // Phương thức lấy danh sách bình luận theo postId với phân trang
    public Page<CommentDTO> getCommentsForPost(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable với số trang và kích thước
        Page<Comment> commentsPage = commentRepository.findByPostId(postId, pageable); // Lấy Page<Comment> với phân trang

        return commentsPage.map(comment -> {
            String username = comment.getUser().getUsername();
            String image = BlobUtil.blobToBase64(comment.getUser().getImage());
            return new CommentDTO(username, comment.getContent(), image); // Ánh xạ Comment thành CommentDTO
        });
    }

    // Phương thức lưu bình luận
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment saveComment(Long postId, String username, String content) {
        // Tìm bài post theo ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Tìm người dùng theo tên người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Tạo một bình luận mới
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        // Chuyển LocalDateTime thành Date
        Date createdAt = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        comment.setCreatedAt(createdAt);

        // Lưu bình luận vào cơ sở dữ liệu
        return commentRepository.save(comment);
    }


}
