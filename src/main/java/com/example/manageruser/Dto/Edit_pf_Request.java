package com.example.manageruser.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Edit_pf_Request {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 6, message = "Username phải có ít nhất 6 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    private String address;


    public @NotBlank(message = "Username không được để trống") @Size(min = 6, message = "Username phải có ít nhất 6 ký tự") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username không được để trống") @Size(min = 6, message = "Username phải có ít nhất 6 ký tự") String username) {
        this.username = username;
    }

    // Getter và Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
