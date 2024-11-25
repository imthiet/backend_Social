package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.ChangePasswordRequest;
import com.example.manageruser.Dto.Edit_pf;
import com.example.manageruser.Dto.Edit_pf_Request;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    // Endpoint để lấy thông tin người dùng
    @GetMapping("/profile")
    public ResponseEntity<Edit_pf> getUserProfile(Principal principal) {
        String username = principal.getName();


        User user = userService.findByUsername(username);


        Edit_pf userProfileDTO = new Edit_pf();
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setAddress(user.getAddress());
        return ResponseEntity.ok(userProfileDTO);
    }

    // Endpoint để cập nhật thông tin người dùng
    @PutMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@Valid @RequestBody Edit_pf_Request userProfileRequest) {
        try {
            User user = userService.updateUserProfile(userProfileRequest); // Giả sử bạn có phương thức cập nhật người dùng
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the profile");
        }
    }
    // Thêm endpoint để thay đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        try {
            String username = principal.getName();

            // Gọi UserService để xử lý thay đổi mật khẩu
            userService.changePassword(username, request);

            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while changing the password");
        }
    }

}