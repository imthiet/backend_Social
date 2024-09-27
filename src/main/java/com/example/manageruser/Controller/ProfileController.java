package com.example.manageruser.Controller;

import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    // profile của người dùng đã đăng nhập
    @GetMapping
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
        }

        User user = userService.findByUsername(username);
        List<User> friends = friendService.getFriends(username);

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);

        return "profile"; //  Thymeleaf
    }

    // Hiển thị trang profile của người dùng khác dựa trên username
    @GetMapping("/{username}")
    public String showUserProfileByUsername(@PathVariable("username") String username, Model model) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return "search"; // Trả về trang lỗi nếu không tìm thấy người dùng
        }

        List<User> friends = friendService.getFriends(username);
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);

        return "profile"; //  Thymeleaf hiển thị profile
    }
}
