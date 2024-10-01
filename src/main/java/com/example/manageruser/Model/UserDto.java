package com.example.manageruser.Model;

public class UserDto {
    private String username;
    private String email;

    // Constructor
    public UserDto(String username, String email) {
        this.username = username;
        this.email = email;
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
}

