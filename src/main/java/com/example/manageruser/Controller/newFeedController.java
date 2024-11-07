package com.example.manageruser.Controller;

import com.example.manageruser.Dto.CommentDTO;
import com.example.manageruser.Model.*;
import com.example.manageruser.Service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.manageruser.Model.NotificationType.LIKE_COMMENT_SHARE;

@Controller
public class newFeedController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    NotificationService notificationService;

    // Hiển thị trang newsfeed
    @GetMapping("/newsfeed")
    public String showNewsFeed(Model model, HttpServletResponse response,
                               Principal principal,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String username = principal.getName();
        User user = userService.findByUsername(username);
        long uID = user.getId();

        // Lấy danh sách bài post từ bạn bè của người dùng hiện tại
        List<Post> posts = postService.getPostsByFriendShip(uID, page, size);

        // Đếm like cho từng bài post và thêm vào model
        Map<Long, Long> likeCounts = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> likeService.countLikesByPostId(post.getId())));

        // Kiểm tra xem từng bài post có được like bởi người dùng hay không
        for (Post post : posts) {
            boolean isLiked = likeService.existsByUserIdAndPostId(user.getId(), post.getId());
            post.setLiked(isLiked); // Cần thêm phương thức setLiked vào class Post
        }

        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("usn", username);
        return "newsfeed";
    }


    // Hiển thị ảnh từ bài post
    @GetMapping("/post/image")
    public ResponseEntity<byte[]> displayPostImage(@RequestParam("id") Long postId) throws SQLException, IOException {
        // Lấy post theo id
        Post post = postService.findPostById(postId);

        // Kiểm tra xem post có ảnh không
        if (post == null || post.getPng() == null) {
            System.out.println("khong có ảnh");
            return ResponseEntity.notFound().build();

        }

        byte[] imageBytes = post.getPng().getBytes(1, (int) post.getPng().length());

        // Trả về ảnh dưới định dạng JPEG (hoặc loại định dạng khác tuỳ vào kiểu ảnh của bạn)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);


    }


    @PostMapping("/comments/add")
    public ResponseEntity<Map<String, Object>> addComment(@RequestParam("postId") Long postId,
                                                          @RequestParam("content") String content,
                                                          Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        Post post = postService.findPostById(postId);

        Map<String, Object> response = new HashMap<>();
        if (user != null && post != null) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setUser(user);
            comment.setPost(post);

            commentService.saveComment(comment);

           // Tạo thông báo
            Notification notification = new Notification();
            notification.setContentnoti(user.getUsername() + " commented on your post.");
            notification.setType(LIKE_COMMENT_SHARE);
            notification.setSender(user);
            notification.setReceiver(post.getUser());
            notification.setStatus("unread");
            notification.setTimestamp(LocalDateTime.now());
            notificationService.save(notification);

            response.put("message", "Comment added successfully");
            response.put("comment", comment);
            return ResponseEntity.ok(response);
        }
        response.put("error", "User or post not found");
        return ResponseEntity.badRequest().body(response);
    }

    // Updated method to get comments by post ID with pagination
    @GetMapping("/comments/post/{postId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,   // Default to page 0
            @RequestParam(defaultValue = "3") int size) { // Default to size 5
        Post post = postService.findPostById(postId);
        if (post != null) {
            // Call the service method with pagination
            Page<CommentDTO> comments = commentService.getCommentsForPost(post.getId(), page, size);
            System.out.println(comments);
            return ResponseEntity.ok(comments); // Return paginated comments
        }
        return ResponseEntity.notFound().build(); // Return 404 if post is not found
    }



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
                response.put("likeCounts", likeService.countLikesByPostId(postId) - 1); // Giảm đi 1
            } else {
                // Người dùng chưa like, thực hiện like
                Like like = new Like();
                like.setUser(user);
                like.setPost(post);
                likeService.save(like);

//             Tạo thông báo
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
                response.put("likeCounts", likeService.countLikesByPostId(postId) + 1); // Tăng thêm 1
            }
            return ResponseEntity.ok(response);
        }
        response.put("error", "User or post not found");
        return ResponseEntity.badRequest().body(response);
    }


}
