package com.example.manageruser.Service;

import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;
import com.example.manageruser.Model.UserWithLastMessageDTO;
import com.example.manageruser.Repository.ChatRepository;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    // Fetch the latest message for the given chatId
    public Optional<Message> findLastMessageByChatId(Long chatId) {
        List<Message> messages = chatRepository.findLastMessageByChatId(chatId, PageRequest.of(0, 1));
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    // Retrieve users with their last message
    public List<UserWithLastMessageDTO> getUsersWithMessages(String username) {
        List<Chat> chats = chatRepository.findByParticipantsUsername(username);
        List<UserWithLastMessageDTO> userWithMessages = new ArrayList<>();

        for (Chat chat : chats) {
            for (User participant : chat.getParticipants()) {
                if (!participant.getUsername().equals(username)) {
                    // Call the service method to fetch the last message
                    Optional<Message> lastMessageOpt = findLastMessageByChatId(chat.getId());

                    UserWithLastMessageDTO dto = new UserWithLastMessageDTO();
                    dto.setUser(participant);
                    lastMessageOpt.ifPresent(dto::setLastMessage); // Set the last message if found

                    userWithMessages.add(dto);
                }
            }
        }
        return userWithMessages;
    }
}
