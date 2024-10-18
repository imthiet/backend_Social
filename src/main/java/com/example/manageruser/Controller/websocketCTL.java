package com.example.manageruser.Controller;

import com.example.manageruser.Model.User;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class websocketCTL {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    UserService userService;

    @MessageMapping("/viewProfile")
    public void notifyProfileViewed(ViewProfileMessage message) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "none";



        // Gửi thông báo tới hàng đợi cá nhân của người dùng được click
        messagingTemplate.convertAndSendToUser(message.getViewer(), "/queue/profileViewed",
                new NotificationMessage(username, "đang xem hồ sơ của bạn!"));

    }

    @MessageMapping("/sendRequest")
    public void notifyRequest(AddFriendMessage message, Principal principal) throws Exception {
        String username = principal.getName();

        // Lấy đối tượng User đang đăng nhập từ UserService

        User currentUser = userService.findByUsername(username);



        // Gửi thông báo tới hàng đợi cá nhân của người dùng được click
        messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/friendRequest",
                new NotificationMessage(currentUser.getUsername(), "Send a friend request"));

    }

    // Helper classes for message handling
    public static class ViewProfileMessage {
        private String viewer;

        public String getViewer() {
            return viewer;
        }

        public void setViewer(String viewer) {
            this.viewer = viewer;
        }
    }

    public static class AddFriendMessage {
        private String sender;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }
    }

    public static class NotificationMessage {
        private String viewer;
        private String message;


        public NotificationMessage( String viewer,String message) {
            this.viewer = viewer;
            this.message = message;

        }



        public String getMessage() {
            return message;
        }

        public String getViewer() {
            return viewer;
        }
    }


    //friend-request
    public static class NotificationRequestMessage {

        private String message;
        private String sender;

        public NotificationRequestMessage ( String sender,String message) {
            this.sender = sender;
            this.message = message;

        }

        public String getMessage() {
            return message;
        }

        public String getSender() {
            return sender;
        }
    }
}
