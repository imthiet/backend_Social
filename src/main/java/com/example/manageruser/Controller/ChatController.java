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
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;

import static com.example.manageruser.Model.NotificationType.LIKE_COMMENT_SHARE;
import static com.example.manageruser.Model.NotificationType.MESSAGE;

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



    @GetMapping("/messages")
    public String showMessagesPage(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            return "redirect:/login"; // If not logged in, redirect to login
        }

        // Get users with their last message
        List<UserWithLastMessageDTO> usersWithMessages = chatService.getUsersWithMessages(username);
        model.addAttribute("usersWithMessages", usersWithMessages);
        model.addAttribute("usn", username);

        return "messages";
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
        // Lưu trữ tin nhắn vào database hoặc xử lý tùy ý tại đây
        return message; // Trả tin nhắn lại cho receiver
    }


    @MessageMapping("/sendMessage")  // Ký tự /app/sendMessage
    @SendTo("/topic/messages")      // Gửi lại tin nhắn cho những người dùng đang subscribe vào /topic/messages
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Tìm đối tượng Chat, Sender và Receiver từ cơ sở dữ liệu
        Chat chat = chatRepository.findById(messageDTO.getChatId()).orElseThrow(() -> new RuntimeException("Chat not found"));
        User sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDTO.getReceiverId()).orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Lưu tin nhắn vào DB
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setChat(chat);
        message.setSender(sender);
        message.setReceiver(receiver);

        // Lưu vào database
        messageRepository.save(message);

        Notification notification = new Notification();
        notification.setContentnoti(sender.getUsername() + " sent you a message");
        notification.setType(MESSAGE);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setStatus("unread");
        notification.setTimestamp(LocalDateTime.now());
        notificationService.save(notification);

        // Trả lại tin nhắn dưới dạng DTO
        return MessageDTO.fromMessage(message);
    }


}
