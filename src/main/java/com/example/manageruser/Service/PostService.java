package com.example.manageruser.Service;

import com.example.manageruser.Dto.PostDTO;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;








//    public List<Post> getAllPosts() {
//        List<Post> posts = postRepository.findAll();
//        return posts;
//
//    }

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private PostDTO convertToDTO(Post post) {
        byte[] imageBytes = null;
        try {
            if (post.getPng() != null) {
                imageBytes = post.getPng().getBytes(1, (int) post.getPng().length());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this error or handling it in a more user-friendly way
        }

        // Ensure createdAt is safely converted and defaults if null
        LocalDateTime createdAt = post.getCreatedAt() != null ?
                post.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now(); // Default to current time if createdAt is null

        return new PostDTO(
                post.getId(),
                null, // Handle this if you need a title, otherwise leave it as null
                post.getContent(),
                imageBytes,
                post.getUser() != null ? post.getUser().getUsername() : "Unknown", // Handle null user
                createdAt
        );
    }

    public List<Post> getPostsByFriendShip(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPostsByFriendship(userId, pageable).getContent();
    }


    public Post findPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public Page<Post> findPostsByUID(long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable với trang và kích thước mong muốn
        return postRepository.getPostsByUserId(id, pageable); // Gọi phương thức repository để lấy danh sách đã phân trang
    }

//old
    public Post findById(Long postId) {
        // Sử dụng Optional để xử lý trường hợp không tìm thấy Post
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));
    }






    public void deleteCourse(Long id) {

        postRepository.deleteById(id);


    }

    public Post updatePost(PostDTO postDTO, Long id) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    // Map fields from PostDTO to Post
                    existingPost.setContent(postDTO.getContent());



                    return postRepository.save(existingPost);
                })
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }


    public boolean deletePostById(Long id) {
        if (postRepository.existsById(id))
        {
            postRepository.deleteById(id);
            return true;
        }
        return false;                         // Trả về false nếu không tìm thấy bài viết
    }
}
