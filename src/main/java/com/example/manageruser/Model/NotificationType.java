package com.example.manageruser.Model;

public enum NotificationType {
    MESSAGE("message"),
    ADD_FRIEND("add-friend"),
    LIKE_COMMENT_SHARE("like-comment-share");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
