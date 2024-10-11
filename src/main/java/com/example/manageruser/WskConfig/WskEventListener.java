package com.example.manageruser.WskConfig;

import java.util.Objects;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.MsgType;
import com.example.manageruser.Model.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WskEventListener {

    private final SimpMessageSendingOperations messagOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (Objects.nonNull(username)) {
            log.info("User disconnected: {}", username);

            messagOperations.convertAndSend("/topic/chat", Message.builder().type(MsgType.LEAVE).sender(username).build());
        }
    }
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    @EventListener
//    public void handleFriendRequestEvent(FriendRequestEvent event) {
//        User sender = event.getSender();
//        User receiver = event.getReceiver();
//
//        // Tạo thông báo kết bạn
//        Message notificationMessage = new Message();
//        notificationMessage.setType(MsgType.FRIEND_REQUEST);
//        notificationMessage.setSender(sender.getUsername());
//        notificationMessage.setContent("You have a new friend request from " + sender.getUsername());
//
//        // Gửi thông báo tới người nhận
//        messagingTemplate.convertAndSendToUser(receiver.getUsername(), "/topic/friendRequest", notificationMessage);
//    }
}
