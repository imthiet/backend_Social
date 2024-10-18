package com.example.manageruser.Controller;

import com.example.manageruser.Dto.NotificationDTO;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

//    // API to get all notifications for the currently logged-in user
//    @GetMapping
//    public Page<Notification> getUserNotifications(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam(defaultValue = "0") int page,   // Page number
//            @RequestParam(defaultValue = "10") int size   // Page size
//    ) {
//        // Get username from Principal
//        String username = userDetails.getUsername();
//
//        // Find user by username
//        User currentUser = userService.findByUsername(username);
//
//        // Fetch notifications by user ID with pagination
//        return notificationService.getNotificationsByReceiverId(currentUser.getId(), page, size);
//    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Principal principal) {
        User user = userService.findByUsername(principal.getName()); // Get the currently logged-in user
        List<Notification> notifications = notificationService.getUnreadNotifications(user); // Fetch unread notifications
        System.out.println("thong bao: " + notifications);
        return ResponseEntity.ok(notifications); // Return the notifications as a response
    }
    // Fetch all notifications for the current user


    @GetMapping("")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = userDetails.getUsername();
        User currentUser = userService.findByUsername(username);
        Page<Notification> notifications = notificationService.getNotificationsByReceiverId(currentUser.getId(), page, size);

        // Map notifications to NotificationDTO
        List<NotificationDTO> notificationDTOs = notifications.getContent().stream()
                .map(noti -> new NotificationDTO(noti.getContentnoti(), noti.getTimestamp(),noti.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificationDTOs); // Return List of DTOs
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User currentUser = userService.findByUsername(username);

        notificationService.markAllAsRead(currentUser.getId());

        return ResponseEntity.ok().build();
    }



}
