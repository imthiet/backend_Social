package com.example.manageruser.Controller;

import com.example.manageruser.Dto.NotificationDTO;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    // Constructor to autowire both NotificationService and SimpMessagingTemplate
    public NotificationController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @GetMapping("/notification")
    public String showNotifications(Authentication authentication, Model model) {
        String username = authentication.getName();  // Get logged-in user
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(username);
        model.addAttribute("notifications", notifications);
        return "notifications";  // This will render notifications.html
    }

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Notification notification) {
        // Kiểm tra xem receiver có phải là null không
        if (notification.getReceiver() != null) {
            User receiverUser = userService.findByUsername(notification.getReceiver().getUsername());
            notification.setReceiver(receiverUser);

            // Gửi thông báo đến người dùng cụ thể
            messagingTemplate.convertAndSendToUser(
                    receiverUser.getUsername(), // tên người nhận
                    "/specific", // Đích đến
                    notification // Thông điệp thông báo sẽ được gửi
            );
            System.out.println("Đang gửi thông báo đến: " + receiverUser.getUsername());
        } else {
            System.out.println("Không tìm thấy người nhận thông báo.");
        }
    }

}
