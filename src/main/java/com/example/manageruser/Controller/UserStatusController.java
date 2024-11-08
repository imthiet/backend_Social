package com.example.manageruser.Controller;

import com.example.manageruser.Dto.UserDTO2;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserStatusController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    // Phương thức trả về trạng thái của người dùng
    @GetMapping("/api/status/{username}")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable String username) {
        Map<String, Object> status = new HashMap<>();

        // Giả sử bạn lấy trạng thái từ cơ sở dữ liệu hoặc session
        boolean isOnline = checkIfUserIsOnline(username);  // Thực hiện logic lấy trạng thái online

        status.put("username", username);
        status.put("online", isOnline);

        return ResponseEntity.ok(status);
    }

    // Phương thức kiểm tra trạng thái online của người dùng, thay thế bằng logic thực tế
    private boolean checkIfUserIsOnline(String username) {
        // Kiểm tra trạng thái người dùng từ DB hoặc nguồn khác
        return true;  // Giả sử người dùng luôn online
    }



}
