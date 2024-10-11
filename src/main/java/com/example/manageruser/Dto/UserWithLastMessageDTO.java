package com.example.manageruser.Dto;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;

public class UserWithLastMessageDTO {
    private User user;
    private Message lastMessage;

    // Getters and setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
