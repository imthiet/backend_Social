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

    private Boolean enabled;

    private Boolean isAdmin;

    public SearchDTO(String username, String email, boolean friend, boolean friendPending, boolean friendRequestReceiver, Boolean enabled, Boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.friend = friend;
        this.friendPending = friendPending;
        this.friendRequestReceiver = friendRequestReceiver;
        this.enabled = enabled;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFriendPending() {
        return friendPending;
    }

    public void setFriendPending(boolean friendPending) {
        this.friendPending = friendPending;
    }

    public boolean isFriendRequestReceiver() {
        return friendRequestReceiver;
    }

    public void setFriendRequestReceiver(boolean friendRequestReceiver) {
        this.friendRequestReceiver = friendRequestReceiver;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isEnable()
    {
        return enabled;
    }
}
