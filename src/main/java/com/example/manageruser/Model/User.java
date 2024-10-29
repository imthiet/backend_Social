package com.example.manageruser.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 5, message = "Username phải có ít nhất 5 ký tự")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    @Column(length = 60, nullable = false)
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Like> likes;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<FriendShip> friendships;

    @OneToMany(mappedBy = "friend", fetch = FetchType.EAGER)
    private List<FriendShip> friends;

    private boolean enabled;
    private String verificationCode;

    private boolean isAdmin = false;

    @Transient
    private boolean friendPending;

    @Transient
    private boolean friend;


    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Lob
    private Blob image;

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public boolean isFriendPending() {
        return friendPending;
    }

    private boolean friendRequestReceiver; // Thêm thuộc tính này

    public void setFriendPending(boolean friendPending) {
        this.friendPending = friendPending;
    }

    public boolean isFriend() {
        return friend;
    }


// Getters và setters


    public boolean isFriendRequestReceiver() {
        return friendRequestReceiver;
    }

    public void setFriendRequestReceiver(boolean friendRequestReceiver) {
        this.friendRequestReceiver = friendRequestReceiver;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<FriendShip> getFriendships() {
        return friendships;
    }

    public void setFriendships(List<FriendShip> friendships) {
        this.friendships = friendships;
    }

    public List<FriendShip> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendShip> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", posts=" + posts +
                ", comments=" + comments +
                ", likes=" + likes +
                ", friendships=" + friendships +
                ", friends=" + friends +
                ", enabled=" + enabled +
                ", verificationCode='" + verificationCode + '\'' +
                ", isAdmin=" + isAdmin +
                ", friendPending=" + friendPending +
                ", address='" + address + '\'' +
                ", image=" + image +
                '}';
    }
}
