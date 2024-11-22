package com.example.manageruser.RestAPI;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.manageruser.Model.NotificationType.MESSAGE;


@RestController
public class FriendShipRestController {
    @Autowired
    SearchService service;

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendShipService;

    @Autowired
    private NotificationService notificationService;


    @PostMapping("/accept_friends")
    public ResponseEntity<Map<String, Object>> acceptFriend(
            @RequestParam("username") String friendUsername, Principal principal) {
        String currentUsername = principal.getName();
        User receiver = userService.findByUsername(currentUsername);
        User sender = userService.findByUsername(friendUsername);

        if (receiver != null && sender != null) {
            FriendShip friendship = friendShipService.findPendingRequest(sender, receiver);
            if (friendship != null && !friendship.isAccepted()) {
                friendship.setAccepted(true);
                friendShipService.save(friendship);

                receiver.setFriendPending(false);
                sender.setFriendPending(false);
                userService.saveAgaint(receiver);
                userService.saveAgaint(sender);

                Notification receiverNotification = new Notification();
                receiverNotification.setContentnoti(
                        "You and " + sender.getUsername() + " are now friends. Lets share amazing things!");
                receiverNotification.setType(MESSAGE);
                receiverNotification.setSender(receiver);
                receiverNotification.setReceiver(receiver);
                receiverNotification.setStatus("unread");
                receiverNotification.setTimestamp(LocalDateTime.now());
                notificationService.save(receiverNotification);

                Notification senderNotification = new Notification();
                senderNotification.setContentnoti(
                        receiver.getUsername() + " accepted your friend request. Lets share amazing things!");
                senderNotification.setType(MESSAGE);
                senderNotification.setSender(sender);
                senderNotification.setReceiver(sender);
                senderNotification.setStatus("unread");
                senderNotification.setTimestamp(LocalDateTime.now());
                notificationService.save(senderNotification);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Friend request accepted");
                response.put("friendshipStatus", "accepted");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(Map.of("error", "No pending request found"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }

}
