package com.example.manageruser.Service;

import com.example.manageruser.Dto.CommentDTO;
import com.example.manageruser.Dto.PostDTO;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Repository.CommentRepository;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

@Autowired
private LikeService likeService;


    public List<PostDTO> getAllPosts(Long userId) {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> convertToDTO(post, userId)).collect(Collectors.toList());
    }




    private PostDTO convertToDTO(Post post, Long userId) {
        byte[] imageBytes = null;
        try {
            if (post.getPng() != null) {
                imageBytes = post.getPng().getBytes(1, (int) post.getPng().length());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this error or handling it in a more user-friendly way
        }

        LocalDateTime createdAt = post.getCreatedAt() != null ?
                post.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now();

        // Convert Comments to CommentDTOs
        List<CommentDTO> commentDTOs = post.getComments().stream()
                .map(comment -> {
                    String userImage = null;
                    try {
                        if (comment.getUser().getImage() != null) {
                            Blob imageBlob = comment.getUser().getImage();
                            byte[] imageData = imageBlob.getBytes(1, (int) imageBlob.length());
                            userImage = Base64.getEncoder().encodeToString(imageData);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return new CommentDTO(
                            comment.getUser().getUsername(),
                            comment.getContent(),
                            userImage
                    );
                })
                .collect(Collectors.toList());

        // Convert List<CommentDTO> to Page<CommentDTO>
        Page<CommentDTO> commentDTOPage = new PageImpl<>(commentDTOs, PageRequest.of(0, 3), commentDTOs.size());

        // Check if the current user has liked the post
        boolean isLiked = likeService.existsByUserIdAndPostId(userId, post.getId());

        // Pass Page<CommentDTO> instead of List<CommentDTO>
        return new PostDTO(
                post.getId(),
                post.getContent(),
                imageBytes,
                post.getUser() != null ? post.getUser().getUsername() : null,
                createdAt,
                post.getLikes().size(),
                commentDTOPage,  // Pass as Page<CommentDTO>
                isLiked
        );
    }

    public List<PostDTO> getPostsByFriendShip(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy danh sách Post từ repository
        List<Post> posts = postRepository.findPostsByFriendship(userId, pageable).getContent();

        // Chuyển đổi từ Post sang PostDTO
        return posts.stream()
                .map(post -> convertToDTO(post, userId))
                .collect(Collectors.toList());
    }




    public Post findPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }

    public Optional<Post> findByPId(Long postId) {
        return postRepository.findById(postId);
    }


    public void save(Post post) {
        postRepository.save(post);
    }

    public Page<Post> findPostsByUID(long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable với trang và kích thước mong muốn
        return postRepository.getPostsByUserId(id, pageable); // Gọi phương thức repository để lấy danh sách đã phân trang
    }
    public List<PostDTO> getUserPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable với trang và kích thước mong muốn
        Page<Post> postsPage = postRepository.getPostsByUserId(userId, pageable); // Lấy dữ liệu phân trang
        List<Post> posts = postsPage.getContent(); // Chuyển đổi từ Page sang List
        return posts.stream().map(post -> convertToDTO(post, userId)).collect(Collectors.toList());
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

    public Post updatePost(Optional<Post> optionalPost, PostDTO postDTO) {
        Post existingPost = optionalPost.get(); // Lấy đối tượng Post từ Optional
        existingPost.setContent(postDTO.getContent()); // Cập nhật nội dung
        return postRepository.save(existingPost); // Lưu vào cơ sở dữ liệu
    }



    public boolean deletePostById(Long id) {
        if (postRepository.existsById(id))
        {
            postRepository.deleteById(id);
            return true;
        }
        return false;                         // Trả về false nếu không tìm thấy bài viết
    }

    public void deleteById(Long postId) {
        postRepository.deleteById(postId);
    }
}
