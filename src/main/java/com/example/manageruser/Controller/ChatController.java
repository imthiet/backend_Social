package com.example.manageruser.Controller;

import com.example.manageruser.Dto.MessageDTO;
import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.Message;
import com.example.manageruser.Dto.UserWithLastMessageDTO;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.ChatRepository;
import com.example.manageruser.Repository.MessageRepository;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.ChatService;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.type.descriptor.jdbc.LocalDateTimeJdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    ChatService chatService;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/chat")
    public Message sendMsg(@Payload Message msg) {
        return msg;
    }

    @MessageMapping("chat.addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message msg, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", msg.getSender());
        return msg;
    }

    @GetMapping("/chat/{chatId}")
    public String openChatBox(@PathVariable Long chatId, Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        model.addAttribute("chatId", chatId);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("currentUserName", currentUser.getUsername());
        return "chatboxx";
    }

    @MessageMapping("/chat/{receiverId}")
    @SendToUser("/queue/messages")
    public Message sendMessage(@DestinationVariable String receiverId, Message message) {
        return message;
    }

    @MessageMapping("/chat/{chatId}/sendMessage")
    @SendTo("/topic/chat/{chatId}")
    public MessageDTO sendMessage(@DestinationVariable Long chatId, @Payload MessageDTO messageDTO) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        User sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDTO.getReceiverId()).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);

        notificationService.createNotification(sender, receiver, "New message");

        messageDTO.setTimestamp(message.getTimestamp());
        // Gửi thông báo tin nhắn tới người gửi và người nhận
//        messagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/messages", messageDTO);
//        messagingTemplate.convertAndSend("/topic/" + chatId, messageDTO);
        return messageDTO;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<UserWithLastMessageDTO>> getMessagesForUser(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserWithLastMessageDTO> usersWithMessages = chatService.getUsersWithMessages(username);

        return ResponseEntity.ok(usersWithMessages);
    }
}
