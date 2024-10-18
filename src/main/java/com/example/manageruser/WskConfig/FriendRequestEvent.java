package com.example.manageruser.WskConfig;

import com.example.manageruser.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public class FriendRequestEvent {
    private final User sender;
    private final User receiver;

    public FriendRequestEvent(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }
}
