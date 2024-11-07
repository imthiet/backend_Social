package com.example.manageruser.Controller;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;

import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import com.example.manageruser.WskConfig.FriendRequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.manageruser.Model.NotificationType.ADD_FRIEND;

@Controller

public class SearchController {

    @Autowired
    SearchService service;

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendShipService;

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
        model.addAttribute("usn", currentUsername);

        return "search";
    }


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private NotificationService notificationService; // Thêm dịch vụ thông báo

    @PostMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam("username") String friendUsername, Principal principal) {
        String currentUsername = principal.getName();
        User sender = userService.findByUsername(currentUsername);
        User receiver = userService.findByUsername(friendUsername);

        if (sender != null && receiver != null) {
            // Check if a friendship already exists
            if (!friendShipService.existsBetweenUsers(sender, receiver)) {
                FriendShip friendShip = new FriendShip();
                friendShip.setUser(sender);
                friendShip.setFriend(receiver);
                friendShip.setAccepted(false);
                friendShipService.save(friendShip);

                // Tạo thông báo
                Notification notification = new Notification();
                notification.setContentnoti(sender.getUsername() + " đã gửi yêu cầu kết bạn cho bạn.!!!!");
                notification.setType(ADD_FRIEND); // Thêm loại thông báo
                notification.setSender(sender); // Đảm bảo 'sender' không null
                notification.setReceiver(receiver);
                notification.setStatus("unread");
                notification.setTimestamp(LocalDateTime.now());

                // Lưu thông báo
                notificationService.save(notification);
            } else {
                return ResponseEntity.badRequest().body("Friendship already exists");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok("Friend request sent");
    }


}
