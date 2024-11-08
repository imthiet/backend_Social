package com.example.manageruser.Dto;

public class UserDTO2 {
    private String username;
    private boolean isOnline;

    public UserDTO2(String username, boolean isOnline) {
        this.username = username;
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
