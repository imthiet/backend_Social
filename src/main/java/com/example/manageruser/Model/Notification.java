package com.example.manageruser.Model;

import jakarta.persistence.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // Nội dung thông báo

    @ManyToOne
    @JoinColumn(name = "receiver_username", referencedColumnName = "username")
    private User receiver; // Người nhận thông báo

    // Constructor không tham số
    public Notification() {}

    // Constructor với tham số
    public Notification(String content, User receiver) {
        this.content = content;
        this.receiver = receiver;
    }

    // Getters và setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", receiverUsername='" + (receiver != null ? receiver.getUsername() : "N/A") + '\'' +
                '}';
    }
}
