package com.example.manageruser.Controller;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Service.PostService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class newFeedController {

    @Autowired
    private PostService postService;

    @GetMapping("/newsfeed")
    public String showNewsFeed(Model model, HttpServletResponse response,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        List<Post> posts = postService.getPostsByFriendShip(username, page, size);
        model.addAttribute("posts", posts);
        model.addAttribute("usn", username);
        model.addAttribute("currentPage", page); // Add current page to model
        return "newsfeed"; // Return the updated newsfeed template
    }
}
