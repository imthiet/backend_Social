package com.example.manageruser.Service;

import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;
import com.example.manageruser.Dto.UserWithLastMessageDTO;
import com.example.manageruser.Repository.ChatRepository;
import com.example.manageruser.Repository.ChatUserRepository;
import com.example.manageruser.Repository.UserRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    private ChatUserRepository chatUserRepository;

    // Fetch the latest message for the given chatId
    public Optional<Message> findLastMessageByChatId(Long chatId) {
        List<Message> messages = chatRepository.findLastMessageByChatId(chatId, PageRequest.of(0, 1));
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    // Phương thức lấy người dùng và tin nhắn cuối cùng
    public List<UserWithLastMessageDTO> getUsersWithMessages(String username) {
        List<Chat> chats = chatRepository.findByParticipantsUsername(username);
        List<UserWithLastMessageDTO> userWithMessages = new ArrayList<>();

        for (Chat chat : chats) {
            for (User participant : chat.getParticipants()) {
                if (!participant.getUsername().equals(username)) {
                    // Get the last message in the chat
                    Optional<Message> lastMessageOpt = findLastMessageByChatId(chat.getId());
                    if (lastMessageOpt.isPresent()) {
                        Message lastMessage = lastMessageOpt.get();
                        // Create DTO with userId and other information
                        UserWithLastMessageDTO dto = new UserWithLastMessageDTO(
                                participant.getUsername(),
                                lastMessage.getTimestamp(),
                                participant.getId(), // Set userId of the participant
                                chat.getId(),
                                lastMessage.getContent()
                        );
                        userWithMessages.add(dto);
                    }
                }
            }
        }
        return userWithMessages;
    }


    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Transactional
    public void deleteChat(Long chatId) {
        // Xóa tất cả các bản ghi liên kết trong bảng chat_user

        chatUserRepository.deleteByChatId(chatId);

        // Xóa bản ghi trong bảng chat
        chatRepository.deleteById(chatId);
    }


}
