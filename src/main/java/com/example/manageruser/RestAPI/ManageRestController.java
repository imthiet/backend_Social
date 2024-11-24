package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Dto.UserManageDto;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.PostService;
import com.example.manageruser.Service.UserService;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/manage")

public class ManageRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private PostService postService;

    @GetMapping("/user/{username}")
    public ResponseEntity<UserManageDto> getUser(@PathVariable("username") String username, Principal principal) {
        // Kiểm tra người dùng đang đăng nhập
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Lấy thông tin người dùng được yêu cầu
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Chuyển đổi User thành UserManageDto
        UserManageDto userManageDto = userService.convertToUserManageDTO(user);

        return new ResponseEntity<>(userManageDto, HttpStatus.OK);
    }

    // Update thông tin người dùng
    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateUser(@PathVariable("username") String username, @RequestBody UserManageDto userDto) {
        try {
            // Lấy thông tin người dùng từ cơ sở dữ liệu
            User existingUser = userService.findByUsername(username);

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Cập nhật thông tin
            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setEnabled(userDto.isEnabled()); // Lấy giá trị enabled từ DTO
            existingUser.setAdmin(userDto.isAdmin());
            existingUser.setVerificationCode(userDto.getVerificationCode());

            // Lưu lại thông tin đã cập nhật
            userService.saveAgaint(existingUser);

            return ResponseEntity.ok("User updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        }
    }





}
