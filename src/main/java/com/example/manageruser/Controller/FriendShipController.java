package com.example.manageruser.Controller;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.User;
import com.example.manageruser.Model.UserDto;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FriendShipController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendShipService;

    @Autowired
    SearchService service;

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String keyword, Pageable pageable) {
        Page<User> userPage = service.listAll(keyword, pageable); // Assuming this returns Page<User>
        List<UserDto> userDtos = userPage.getContent().stream()
                .map(user -> new UserDto(user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }



    @PostMapping("/add_friend")
    public String addFriend(@RequestParam("username") String friendUsername, Principal principal, Model model) {
        // Lấy thông tin người dùng hiện tại
        String currentUsername = principal.getName();

        User currentUser = userService.findByUsername(currentUsername);

        // Lấy thông tin người dùng được kết bạn
        User friendUser = userService.findByUsername(friendUsername);

        if (currentUser != null && friendUser != null) {
            // Kiểm tra xem yêu cầu kết bạn đã tồn tại chưa
            if (!friendShipService.existsBetweenUsers(currentUser, friendUser)) {
                // Tạo mới đối tượng FriendShip
                FriendShip friendShip = new FriendShip();
                friendShip.setUser(currentUser);
                friendShip.setFriend(friendUser);
                friendShip.setAccepted(false);
                friendShipService.save(friendShip);
            }
        }

        // Chuyển hướng lại trang tìm kiếm với từ khóa
        return "redirect:/search?keyword=" + URLEncoder.encode(" ", StandardCharsets.UTF_8);
    }



}
