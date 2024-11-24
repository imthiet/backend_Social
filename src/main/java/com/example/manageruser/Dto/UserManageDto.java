package com.example.manageruser.Dto;

public class UserManageDto {

    private Long id;
    private String username;
    private String verificationCode;
    private Boolean enabled;
    private String email;
    private Boolean isAdmin;

    public UserManageDto(Long id, String username, String verificationCode, Boolean enabled, String email, Boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.verificationCode = verificationCode;
        this.enabled = enabled;
        this.email = email;
        this.isAdmin = isAdmin;
    }





    // Getter và Setter

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
