package com.example.manageruser.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(length = 60,nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<FriendShip> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendShip> friends) {
        this.friends = friends;
    }

    public List<FriendShip> getFriendships() {
        return friendships;
    }

    public void setFriendships(List<FriendShip> friendships) {
        this.friendships = friendships;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
