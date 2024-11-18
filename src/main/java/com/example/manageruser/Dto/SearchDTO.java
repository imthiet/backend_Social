package com.example.manageruser.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {
    private String username;
    private String email;
    private boolean friend; // Đã là bạn
    private boolean friendPending; // Đang chờ kết bạn
    private boolean friendRequestReceiver; // Là người nhận lời mời kết bạn

    public SearchDTO(String username, String email, boolean friend, boolean friendPending, boolean friendRequestReceiver) {
        this.username = username;
        this.email = email;
        this.friend = friend;
        this.friendPending = friendPending;
        this.friendRequestReceiver = friendRequestReceiver;
    }
}
