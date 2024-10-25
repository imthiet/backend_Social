package com.example.manageruser.Controller;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.PostService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostService postService;

    // Hiển thị profile của người dùng đã đăng nhập
    @GetMapping
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        User u = userService.findByUsername(username);
        int u_id = u.getId();

        if (username == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return "redirect:/login"; // Nếu người dùng không tồn tại, chuyển về trang login
        }

        List<User> friends = friendService.getFriends(username);
        List<Post> userPosts = postService.findPostsByUID(u_id); // Lấy các bài post của người dùng

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("userPosts", userPosts); // Truyền danh sách bài viết cho view

        return "profile"; // Trả về view profile với thông tin người dùng và bạn bè
    }

    // Hiển thị profile của người dùng khác dựa trên username
    @GetMapping("/{username}")
    public String showUserProfileByUsername(@PathVariable("username") String username, Model model) {
        User user = userService.findByUsername(username);
        int u_id = user.getId();
        System.out.println("user id:");

        if (user == null) {
            return "redirect:/search"; // Trả về trang tìm kiếm nếu không tìm thấy người dùng
        }

        List<User> friends = friendService.getFriends(username);
        List<Post> userPosts = postService.findPostsByUID(u_id); // Lấy các bài post của người dùng
//        System.out.println("user post: "+ userPosts);

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("userPosts", userPosts); // Truyền danh sách bài viết cho view

        return "profile_view"; // Trả về view profile với thông tin người dùng, bạn bè, và bài viết
    }

    // Upload avatar cho người dùng
    @PostMapping("/{id}/avatar")
    public String uploadAvatar(@PathVariable("id") int userId, @RequestParam("file") MultipartFile file) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return "redirect:/profile?error=userNotFound";
            }

            // Chỉ chấp nhận các file ảnh có định dạng MIME type là image
            if (!file.getContentType().startsWith("image/")) {
                return "redirect:/profile?error=invalidFileFormat"; // Nếu không phải định dạng ảnh, báo lỗi
            }

            // Chuyển file thành Blob để lưu vào database
            byte[] bytes = file.getBytes();
            Blob blob = new SerialBlob(bytes);
            user.setImage(blob); // Lưu Blob vào thuộc tính image của người dùng

            userService.saveAgaint(user);  // Cập nhật lại người dùng với avatar mới
            return "redirect:/profile";  // Điều hướng về trang profile sau khi upload thành công
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return "redirect:/profile?error=uploadFailed"; // Điều hướng về profile nếu upload thất bại
        }
    }

    // Hiển thị ảnh avatar từ database
    @GetMapping("/display")

    public ResponseEntity<byte[]> displayImage(@RequestParam("id") int userId) throws SQLException, IOException {
        // Lấy user theo id
        User user = userService.findById(userId);

        // Kiểm tra xem user có tồn tại và có ảnh trong cơ sở dữ liệu hay không
        if (user == null || user.getImage() == null) {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không có ảnh
        }

        // Chuyển đổi Blob thành byte[]
        byte[] imageBytes = user.getImage().getBytes(1, (int) user.getImage().length());

        // Trả về ảnh dưới định dạng JPEG (hoặc có thể thay đổi tùy loại ảnh bạn đang lưu)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
}

