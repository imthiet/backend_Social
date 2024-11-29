package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.CommentDTO;
import com.example.manageruser.Dto.PostDTO;
import com.example.manageruser.Model.Like;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.manageruser.Model.NotificationType.LIKE_COMMENT_SHARE;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/post")

public class PostRestController {

    private final UserService userService;
    private final PostService postService;
    private final LikeService likeService;
    private final NotificationService notificationService;
    private final PostRepository postRepository;

    private final CommentService commentService;

    public PostRestController(UserService userService, PostService postService, LikeService likeService, NotificationService notificationService,CommentService commentService, PostRepository postRepository) {
        this.userService = userService;
        this.postService = postService;
        this.likeService = likeService;
        this.notificationService = notificationService;
        this.commentService = commentService;
        this.postRepository = postRepository;
    }
    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPost(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        long userId = user.getId();

        List<PostDTO> postDTOs = postService.getAllPosts(userId).stream()
                .map(post -> {
                    PostDTO postDTO = new PostDTO();
                    postDTO.setId(post.getId());
                    postDTO.setContent(post.getContent());
                    postDTO.setImage(post.getImage());
                    postDTO.setCreatedBy(post.getCreatedBy());
                    postDTO.setCreatedAt(post.getCreatedAt());
                    postDTO.setLikesCount(likeService.countLikesByPostId(post.getId()));
                    postDTO.setLiked(likeService.existsByUserIdAndPostId(userId, post.getId())); // Set isLiked status

                    // Sử dụng service để lấy danh sách comments phân trang và ánh xạ thành Page<CommentDTO>
                    Page<CommentDTO> commentDTOs = commentService.getCommentsForPost(post.getId(), 0, 3); // Giả sử trang 0 và size 3
                    postDTO.setComments(commentDTOs);

                    return postDTO;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDTOs, HttpStatus.OK);
    }

    @GetMapping("/all_p")
    public ResponseEntity<Map<String, Long>> getPostCountByMonth(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        long userId = user.getId();

        // Lấy danh sách tất cả PostDTO
        List<PostDTO> postDTOs = postService.getAllPosts(userId);

        // Nhóm bài viết theo tháng từ trường createdAt trong PostDTO
        Map<String, Long> postCountByMonth = postDTOs.stream()
                .collect(Collectors.groupingBy(
                        postDTO -> formatMonth(postDTO.getCreatedAt()), // Chuyển đổi LocalDateTime thành tên tháng
                        Collectors.counting() // Đếm số lượng bài viết trong từng nhóm
                ));

        return new ResponseEntity<>(postCountByMonth, HttpStatus.OK);
    }

    // Hàm chuyển đổi LocalDateTime thành tên tháng
    private String formatMonth(LocalDateTime dateTime) {
        return dateTime.getMonth().toString(); // Trả về tên tháng (JANUARY, FEBRUARY, ...)
    }



    @GetMapping("/postByFriend")
        public ResponseEntity<List<PostDTO>> getPostByFriend(
                Principal principal,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "7") int size) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            long userId = user.getId();

            // Gọi service để lấy danh sách PostDTO
            List<PostDTO> postDTOs = postService.getPostsByFriendShip(userId,page,size).stream()
                    .map(post -> {
                        PostDTO postDTO = new PostDTO();
                        postDTO.setId(post.getId());
                        postDTO.setContent(post.getContent());
                        postDTO.setImage(post.getImage());
                        postDTO.setCreatedBy(post.getCreatedBy());
                        postDTO.setCreatedAt(post.getCreatedAt());
                        postDTO.setLikesCount(likeService.countLikesByPostId(post.getId()));
                        postDTO.setLiked(likeService.existsByUserIdAndPostId(userId, post.getId())); // Set isLiked status

                        // Sử dụng service để lấy danh sách comments phân trang và ánh xạ thành Page<CommentDTO>
                        Page<CommentDTO> commentDTOs = commentService.getCommentsForPost(post.getId(), 0, 4); // Giả sử trang 0 và size 3
                        postDTO.setComments(commentDTOs);

                        return postDTO;
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(postDTOs, HttpStatus.OK);
        }




//    @DeleteMapping("/delete/{id}")
//    public void deletePost(@PathVariable  Long id)
//    {
//        postService.deleteCourse(id);
//    }

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> likePost(@RequestParam("postId") Long postId, Principal principal) {
        String currentUsername = principal.getName();
        User user = userService.findByUsername(currentUsername);
        Post post = postService.findById(postId);

        Map<String, Object> response = new HashMap<>();
        if (user != null && post != null) {
            if (likeService.existsByUserIdAndPostId(user.getId(), post.getId())) {
                // Người dùng đã like, thực hiện xóa like
                likeService.deleteByUserIdAndPostId(user.getId(), post.getId());
                response.put("message", "Post unliked");
                response.put("isLiked", false);
                response.put("likeCounts", likeService.countLikesByPostId(postId));
            } else {
                // Người dùng chưa like, thực hiện like
                Like like = new Like();
                like.setUser(user);
                like.setPost(post);
                likeService.save(like);

                // Tạo thông báo
                Notification notification = new Notification();
                notification.setContentnoti(user.getUsername() + " đã thích bài đăng của bạn.");
                notification.setType(LIKE_COMMENT_SHARE);
                notification.setSender(user);
                notification.setReceiver(post.getUser());
                notification.setStatus("unread");
                notification.setTimestamp(LocalDateTime.now());
                notificationService.save(notification);

                response.put("message", "Post liked");
                response.put("isLiked", true);
                response.put("likeCounts", likeService.countLikesByPostId(postId));
            }
            return ResponseEntity.ok(response);
        }
        response.put("error", "User or post not found");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    public Post getPostByID(@PathVariable Long id)
    {
        return postService.findById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        Optional<Post> post = postService.findByPId(id);
        if (post.isPresent()) {
            // Gọi updatePost và trả về bài viết đã cập nhật
            Post updatedPost = postService.updatePost(post, postDTO);
            return ResponseEntity.ok(updatedPost);
        } else {
            // Trả về lỗi 404 nếu bài viết không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }



}
