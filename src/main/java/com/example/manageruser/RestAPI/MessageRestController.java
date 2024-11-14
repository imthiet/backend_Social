package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.MessageDTO;
import com.example.manageruser.Dto.UserWithLastMessageDTO;
import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.ChatRepository;
import com.example.manageruser.Repository.MessageRepository;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.ChatService;
import com.example.manageruser.Service.MessageService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/chat")
public class MessageRestController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;



    // Lấy danh sách các tin nhắn theo chatId
    @GetMapping("/{chatId}")
    public ResponseEntity<Page<MessageDTO>> getMessagesByChatId(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("timestamp"))); // Sắp xếp theo thời gian giảm dần
        Page<Message> messagesPage = messageRepository.findByChatId(chatId, pageable);

        if (messagesPage.hasContent()) {
            List<MessageDTO> messageDTOList = messagesPage.stream()
                    .map(message -> {
                        String senderUsername = userService.getUsernameById(message.getSender().getId());
                        return new MessageDTO(
                                message.getId(),
                                message.getContent(),
                                message.getChat().getId(),
                                message.getTimestamp(),
                                message.getSender().getId(),
                                message.getReceiver().getId(),
                                senderUsername
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(messageDTOList, pageable, messagesPage.getTotalElements()));
        } else {
            return ResponseEntity.noContent().build();
        }
    }



    @PostMapping("/createChat/{receiverUsername}")
    public ResponseEntity<Long> createChat(@PathVariable String receiverUsername, Principal principal) {
        // Lấy user hiện tại từ principal
        // Lấy user hiện tại từ principal
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

// Lấy receiver từ username được truyền vào
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));


        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body(null); // Trả về lỗi nếu không tìm thấy người dùng
        }

        // Kiểm tra nếu chat đã tồn tại giữa 2 người này chưa
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(sender.getId(), receiver.getId());
        if (existingChat.isPresent()) {
            return ResponseEntity.ok(existingChat.get().getId()); // Nếu đã có chat, trả về ID của chat cũ
        }

        // Nếu không có chat, tạo một chat mới
        Chat newChat = Chat.builder()
                .name(sender.getUsername() + "-" + receiver.getUsername())
                .isGroup(false)  // Chat giữa 2 người thì không phải nhóm
                .participants(Arrays.asList(sender, receiver))
                .build();

        chatRepository.save(newChat);
        // Gửi tin nhắn mặc định sau khi tạo chat
        String defaultMessage = "We are friends! Let's talk!";

        Message message = new Message();
        message.setChat(newChat);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(defaultMessage);
        message.setTimestamp(LocalDateTime.now());

        Message message1 = new Message();
        message1.setChat(newChat);
        message1.setSender(receiver);
        message1.setReceiver(sender);
        message1.setContent(defaultMessage);
        message1.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
        messageRepository.save(message1);
        // Lưu tin nhắn vào cơ sở dữ liệu


        return ResponseEntity.ok(newChat.getId());  // Trả về ID của chat mới
    }


    // API trả về danh sách người dùng với tin nhắn cuối cùng của họ
    @GetMapping("/messages")
    public ResponseEntity<List<UserWithLastMessageDTO>> getMessagesForUser(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Nếu người dùng chưa đăng nhập
        }

        // Lấy danh sách người dùng với tin nhắn cuối cùng
        List<UserWithLastMessageDTO> usersWithMessages = chatService.getUsersWithMessages(username);

        return ResponseEntity.ok(usersWithMessages); // Trả về danh sách người dùng và tin nhắn
    }

//    @PostMapping("/api/chat/{chatId}/messages")
//    public ResponseEntity<MessageDTO> sendMessage(
//            @PathVariable Long chatId,
//            @RequestBody MessageDTO messageDTO) {
//        Chat chat = chatService.findById(chatId); // Sử dụng phương thức findById đã được sửa
//        if (chat == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Trả về 404 nếu chat không tồn tại
//        }
//
//        User sender = userService.findById(messageDTO.getSenderId());
//        User receiver = userService.findById(messageDTO.getReceiverId());
//
//        if (sender == null || receiver == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Trả về 404 nếu sender hoặc receiver không tồn tại
//        }
//
//        Message message = new Message();
//        message.setContent(messageDTO.getContent());
//        message.setTimestamp(LocalDateTime.now());
//        message.setChat(chat);
//        message.setSender(sender);
//        message.setReceiver(receiver);
//
//        Message savedMessage = messageService.save(message); // Lưu và nhận lại Message đã được lưu
//        return ResponseEntity.ok(MessageDTO.fromMessage(savedMessage)); // Trả về MessageDTO
//    }



}

