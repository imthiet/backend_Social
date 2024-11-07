package com.example.manageruser.Controller;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.LikeService;
import com.example.manageruser.Service.PostService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

     // profile của người dùng khac
    @GetMapping("/{username}")
    public String showUserProfileByUsername(@PathVariable("username") String username,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            Model model,
                                            Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/noti_list"; // Trả về trang tìm kiếm nếu không tìm thấy người dùng
        }

        // Lấy danh sách bạn bè của người dùng
        List<User> friends = friendService.getFriends(username);
        model.addAttribute("friends", friends);

        user.setFriend(friendService.isFriendAccepted(currentUser, user));
//        System.out.println("curent Use setfrr: " + currentUser + "User: " + user);
//        System.out.println(("isFriend: " + user.isFriend()));

        user.setFriendPending(friendService.isFriendPending(currentUser, user));
//        System.out.println("curent User pending: " + currentUser + "User: " + user);
//        System.out.println("isFriendPending: " + user.isFriendPending());




        boolean isReceiver = friendService.isCurrentUserFriendRequestReceiver(user, currentUser);


        user.setFriendRequestReceiver(isReceiver);

        Page<Post> userPostsPage = postService.findPostsByUID(user.getId(), page, size); // Lấy các bài post của người dùng với phân trang

        // Đếm like cho từng bài post và thêm vào model
        Map<Long, Long> likeCounts = userPostsPage.stream()
                .collect(Collectors.toMap(Post::getId, post -> likeService.countLikesByPostId(post.getId())));

        // Kiểm tra xem từng bài post có được like bởi người dùng hay không
        for (Post post : userPostsPage) {
            boolean isLiked = likeService.existsByUserIdAndPostId(currentUser.getId(), post.getId());
            post.setLiked(isLiked); // Cần thêm phương thức setLiked vào class Post
        }

        model.addAttribute("user", user);
        model.addAttribute("likeCounts",likeCounts);
        model.addAttribute("userPosts", userPostsPage.getContent()); // Truyền danh sách bài viết cho view
        model.addAttribute("currentPage", userPostsPage.getNumber()); // Trang hiện tại
        model.addAttribute("totalPages", userPostsPage.getTotalPages()); // Tổng số trang

        return "profile_view"; // Trả về view profile với thông tin người dùng, bạn bè, và bài viết
    }



    // Hiển thị profile của người dùng hien tai
    @GetMapping
    public String showUserProfile(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        User u = userService.findByUsername(username);
        long u_id = u.getId();

        if (username == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return "redirect:/login"; // Nếu người dùng không tồn tại, chuyển về trang login
        }

        List<User> friends = friendService.getFriends(username);
        Page<Post> userPostsPage = postService.findPostsByUID(u_id, page, size); // Lấy các bài post của người dùng với phân trang

        model.addAttribute("user", user);

        model.addAttribute("friends", friends);
        model.addAttribute("userPosts", userPostsPage.getContent()); // Truyền danh sách bài viết cho view
        model.addAttribute("currentPage", userPostsPage.getNumber()); // Trang hiện tại
        model.addAttribute("totalPages", userPostsPage.getTotalPages()); // Tổng số trang

        return "profile"; // Trả về view profile với thông tin người dùng và bạn bè
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

