package com.example.manageruser.Dto;

import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notifications")
public class NotificationControllerSC {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // Phương thức để tạo thông báo
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        // Kiểm tra người nhận
        User receiver = userService.findByUsername(notification.getReceiver().getUsername());
        if (receiver == null) {
            return ResponseEntity.badRequest().build(); // Trả về 400 nếu người nhận không tồn tại
        }

        // Tạo thông báo
        notification.setReceiver(receiver);
        Notification savedNotification = notificationService.save(notification);
        return ResponseEntity.ok(savedNotification);
    }

    // Phương thức để lấy thông báo cho người dùng
    @GetMapping("/{username}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String username) {
        List<Notification> notifications = notificationService.findByReceiverUsername(username);
        return ResponseEntity.ok(notifications);
    }
}
