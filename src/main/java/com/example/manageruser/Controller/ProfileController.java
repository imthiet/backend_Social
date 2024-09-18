package com.example.manageruser.Controller;


import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @GetMapping
    public String showUserProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/users/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
        }

        User user = userService.findByUsername(username);
        List<User> friends = friendService.getFriends(username);

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);

        return "profile"; // Tên của template Thymeleaf
    }
}
