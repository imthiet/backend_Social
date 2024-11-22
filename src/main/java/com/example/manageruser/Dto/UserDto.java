package com.example.manageruser.Dto;

public class UserDto {
    private String username;
    private String email;
    private boolean isFriend;
    private boolean isFriendPending;
    private String image;
    private boolean friendRequestReceiver; // Là người nhận lời mời kết bạn
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Constructor

    public UserDto(String username, String email, boolean isFriend, boolean isFriendPending, String image, boolean friendRequestReceiver) {
        this.username = username;
        this.email = email;
        this.isFriend = isFriend;
        this.isFriendPending = isFriendPending;
        this.image = image;
        this.friendRequestReceiver = friendRequestReceiver;
    }

    public UserDto() {
        // No-argument constructor
    }


    // Getters and Setters
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
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isFriendPending() {
        return isFriendPending;
    }

    public void setFriendPending(boolean isFriendPending) {
        this.isFriendPending = isFriendPending;
    }

    public boolean isFriendRequestReceiver() {
        return friendRequestReceiver;
    }

    public void setFriendRequestReceiver(boolean friendRequestReceiver) {
        this.friendRequestReceiver = friendRequestReceiver;
    }
}
