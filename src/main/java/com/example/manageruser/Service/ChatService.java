package com.example.manageruser.Service;

import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.ChatRepository;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsersWithMessages(String username) {
        // Tìm các cuộc trò chuyện mà người dùng hiện tại tham gia
        List<Chat> chats = chatRepository.findByParticipantsUsername(username);

        // Tạo một Set để lưu các người dùng (để tránh trùng lặp)
        Set<User> users = new HashSet<>();

        // Duyệt qua từng cuộc trò chuyện
        for (Chat chat : chats) {
            // Duyệt qua từng người dùng trong cuộc trò chuyện
            for (User participant : chat.getParticipants()) {
                // Nếu người dùng không phải là người dùng hiện tại, thêm vào Set
                if (!participant.getUsername().equals(username)) {
                    users.add(participant);
                }
            }
        }
        System.out.println("Users found: " + users.size() +" _ " + users.toString());

        // Chuyển đổi Set sang List và trả về
        return new ArrayList<>(users);
    }
}
