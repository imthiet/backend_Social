package com.example.manageruser.WskConfig;

import com.example.manageruser.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestEvent {
    private User sender;
    private User receiver;
}
