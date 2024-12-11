package com.example.manageruser.RestAPI;

import com.example.manageruser.Repository.CommentRepository;
import com.example.manageruser.Repository.MessageRepository;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class ChartAPI {

    @Autowired
    private PostRepository postRepository; // Đổi tên cho thống nhất

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Lượt tương tác: Số lượng comment và like theo tháng
    @GetMapping("/interaction")
    public ResponseEntity<Map<String, Object>> getInteractionStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Lấy dữ liệu từ repository
        List<Object[]> data = postRepository.getInteractionStatistics(); // Đảm bảo phương thức này có trong PostRepository

        for (Object[] row : data) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("year", row[0]);
            entry.put("month", row[1]);
            entry.put("comment_count", row[2]);
            entry.put("like_count", row[3]);
            result.add(entry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    // Tin nhắn: Số lượng tin nhắn theo tháng
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessagesStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Lấy dữ liệu từ repository
        List<Object[]> data = messageRepository.getMessageStatistics(); // Đảm bảo phương thức này có trong MessageRepository

        for (Object[] row : data) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("year", row[0]);
            entry.put("month", row[1]);
            entry.put("message_count", row[2]);
            result.add(entry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    // Người dùng theo địa chỉ: Số lượng người dùng theo từng địa chỉ
    @GetMapping("/users-by-address")
    public ResponseEntity<Map<String, Object>> getUsersByAddress() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Lấy dữ liệu từ repository
        List<Object[]> data = userRepository.getUsersByAddress(); // Đảm bảo phương thức này có trong UserRepository

        for (Object[] row : data) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("address", row[0]);
            entry.put("user_count", row[1]);
            result.add(entry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

}
