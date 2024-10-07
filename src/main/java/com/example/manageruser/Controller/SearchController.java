package com.example.manageruser.Controller;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@Controller

public class SearchController {

    @Autowired
    SearchService service;

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendShipService;

    //    @RequestMapping("/search-exit")
//    public String index(Model model, @Param("keyword") String keyword){
//        List<User> listUser = service.listAll(keyword);
//
//        // Thay đổi tên biến từ studentList thành listStudent để khớp với Thymeleaf
//        model.addAttribute("listUser", listUser);
//        model.addAttribute("keyword", keyword);
//
//        return "search"; // template search
//    }
    @GetMapping("/search_page")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Principal principal, Model model) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            usersPage = service.listAll(keyword, pageable, currentUser);
            model.addAttribute("label_rs", "Kết quả tìm kiếm");
        } else if (keyword == null || keyword == " ") {
            usersPage = service.listAll(null, pageable, currentUser);

        } else {
            // Directly suggest friends if no keyword is provided
            usersPage = service.suggestFriends(currentUser, pageable);
            model.addAttribute("label_rs", "Gợi ý bạn bè");
        }

        usersPage.getContent().forEach(user -> {
            user.setFriendPending(friendShipService.isFriendPending(currentUser, user));
        });

        model.addAttribute("listUser", usersPage.getContent());
        model.addAttribute("currentPage", usersPage.getNumber());
        model.addAttribute("totalPages", usersPage.getTotalPages());

        return "search";
    }

    @PostMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam("username") String friendUsername, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);
        User friendUser = userService.findByUsername(friendUsername);

        if (currentUser != null && friendUser != null) {
            if (!friendShipService.existsBetweenUsers(currentUser, friendUser)) {
                FriendShip friendShip = new FriendShip();
                friendShip.setUser(currentUser);
                friendShip.setFriend(friendUser);
                friendShip.setAccepted(false);
                friendShipService.save(friendShip);
            }
        }

        return ResponseEntity.ok("Friend request sent");
    }

}
