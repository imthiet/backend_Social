package com.example.manageruser.Controller;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.PostService;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.rowset.serial.SerialBlob;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // API tạo post mới
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPost(@RequestParam("content") String content,
                                                          @AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Lấy thông tin người dùng từ session
            String username = userDetails.getUsername();
            User currentUser = userService.findByUsername(username);
            System.out.println("user " + currentUser);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Kiểm tra nếu có ảnh được upload
            Blob blob = null;
            if (!file.isEmpty() && file.getContentType().startsWith("image/")) {
                byte[] bytes = file.getBytes();
                blob = new SerialBlob(bytes);
            }

            // Tạo bài post mới
            Post post = new Post();
            post.setContent(content);
            post.setCreatedAt(new Date(System.currentTimeMillis())); // Lưu thời gian hiện tại
            post.setUser(currentUser); // Liên kết post với người dùng
            post.setPng(blob);  // Lưu ảnh (nếu có)

            // Lưu bài post
            postService.save(post);

            // Phản hồi thành công
            response.put("success", true);
            response.put("message", "Post created successfully!");
            return ResponseEntity.ok(response); // Trả về JSON với thông báo thành công

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to create post");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Trả về JSON với thông báo lỗi
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        boolean isDeleted = postService.deletePostById(id);

        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Post not found"));
        }
    }

}
