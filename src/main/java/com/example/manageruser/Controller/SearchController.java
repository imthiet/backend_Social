package com.example.manageruser.Controller;

import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public String searchUsers(@RequestParam("query") String query, Model model) {
        // Tìm kiếm người dùng dựa trên từ khóa
        List<User> searchResults = userRepository.findByUsernameContainingIgnoreCase(query);

        // Đưa kết quả tìm kiếm vào Model để hiển thị trong view
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);

        // Trả về trang kết quả tìm kiếm

        return "search-results";
    }
}
