package com.example.manageruser.Controller;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Service.PostService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class newFeedController {

    @Autowired
    private PostService postService;

    @GetMapping("/newsfeed")
    public String showNewsFeed(Model model, HttpSession session, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/users/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
        }

       List<Post> posts = postService.getPostsByFriendShip(username);
       //List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("usn", username);
        return "newsfeed";
    }

}
