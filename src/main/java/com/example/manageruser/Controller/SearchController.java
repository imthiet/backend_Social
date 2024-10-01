package com.example.manageruser.Controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String search(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Principal principal, Model model) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = service.listAll(keyword, pageable);

        // Gán thêm thông tin về tình trạng kết bạn
        usersPage.getContent().forEach(user -> {

            user.setFriendPending(friendShipService.isFriendPending(currentUser, user));
        });

        model.addAttribute("listUser", usersPage.getContent());
        model.addAttribute("currentPage", usersPage.getNumber());
        model.addAttribute("totalPages", usersPage.getTotalPages());
        return "search";
    }
}
