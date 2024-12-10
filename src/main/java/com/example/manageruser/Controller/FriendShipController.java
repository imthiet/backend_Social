package com.example.manageruser.Controller;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.manageruser.Model.NotificationType.ADD_FRIEND;
import static com.example.manageruser.Model.NotificationType.MESSAGE;

@Controller
public class FriendShipController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendShipService;

    @Autowired
    private SearchService service;
    @Autowired
    private FriendService friendService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String keyword,
                                                     @RequestParam int page,
                                                     @RequestParam int size,
                                                     Principal principal) {

        Pageable pageable = PageRequest.of(page, size); // Create pageable object
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername); // Lấy người dùng hiện tại

        // Truyền currentUser vào lời gọi phương thức listAll
        Page<User> usersPage = service.listAll(keyword, pageable, currentUser); // Get the Page<User>

        // Convert User to DTO and check friend status
        List<UserDto> userDTOs = usersPage.getContent().stream().map(user -> {
            return createUserDto(user, currentUser);
        }).collect(Collectors.toList());

        // Handle empty results
        if (userDTOs.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Return empty list if no users found
        }

        return ResponseEntity.ok(userDTOs);
    }


    private UserDto createUserDto(User user, User currentUser) {
        UserDto userDTO = new UserDto(user.getUsername(), user.getEmail(), false, false, null, false);

        // Check friendship status
        boolean isFriendPending = friendShipService.isFriendPending(currentUser, user);
        boolean isFriend = friendShipService.isFriendAccepted(currentUser, user); // Check if they are friends

        userDTO.setFriendPending(isFriendPending);
        userDTO.setFriend(isFriend);

        // Optionally set the user's image if available
        if (user.getImage() != null) {
            userDTO.setImage(getBase64Image(user.getImage())); // Assume this method exists
        }

        return userDTO;
    }

    // Add method to convert BLOB to Base64 string if needed
    private String getBase64Image(Blob imageBlob) {
        try {
            if (imageBlob != null) {
                byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes); // Adjust MIME type if necessary
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for troubleshooting
        }
        return null; // Return null if the image is not available
    }

    @PostMapping("/accept_friend")
    public ResponseEntity<String> acceptFriend(@RequestParam("username") String friendUsername, Principal principal) {
        String currentUsername = principal.getName();
        User receiver = userService.findByUsername(currentUsername);
        User sender = userService.findByUsername(friendUsername);

        if (receiver != null && sender != null) {
            FriendShip friendship = friendShipService.findPendingRequest(sender, receiver);
            if (friendship != null && !friendship.isAccepted()) {
                friendship.setAccepted(true);
                friendShipService.save(friendship);

                // Cập nhật isFriendPending của cả người nhận và người gửi
                receiver.setFriendPending(false);
                sender.setFriendPending(false);
                userService.saveAgaint(receiver);
                userService.saveAgaint(sender);

                // Tạo thông báo cho receiver
                Notification receiverNotification = new Notification();
                receiverNotification.setContentnoti("You and " + sender.getUsername() + " are now friends. Lets share amazing things!");
                receiverNotification.setType(MESSAGE);
                receiverNotification.setSender(receiver);
                receiverNotification.setReceiver(receiver);
                receiverNotification.setStatus("unread");
                receiverNotification.setTimestamp(LocalDateTime.now());
                notificationService.save(receiverNotification);

                // Tạo thông báo cho sender
                Notification senderNotification = new Notification();
                senderNotification.setContentnoti(receiver.getUsername() + " accepted your friend request. Lets share amazing things!");
                senderNotification.setType(MESSAGE);
                senderNotification.setSender(sender);
                senderNotification.setReceiver(sender);  // Gửi thông báo đến chính sender
                senderNotification.setStatus("unread");
                senderNotification.setTimestamp(LocalDateTime.now());
                notificationService.save(senderNotification);

                return ResponseEntity.ok("Friend request accepted");
            }
            return ResponseEntity.badRequest().body("No pending request found");
        }
        return ResponseEntity.badRequest().body("User not found");
    }


}
