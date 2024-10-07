package com.example.manageruser.Controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class FriendShipController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendShipService;

    @Autowired
    private SearchService service;

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
        UserDto userDTO = new UserDto(user.getUsername(), user.getEmail(), false, false, null);

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


}
