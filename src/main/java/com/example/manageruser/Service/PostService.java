package com.example.manageruser.Service;

import com.example.manageruser.Dto.PostDTO;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Repository.PostRepository;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;



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
            e.printStackTrace();
        }

        return new PostDTO(
                post.getId(),
                null, // Vì `Post` không có thuộc tính `title`, bạn có thể bỏ qua hoặc thiết lập giá trị khác nếu cần
                post.getContent(),
                imageBytes,
                post.getUser() != null ? post.getUser().getUsername() : null,
                post.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
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
