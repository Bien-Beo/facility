package com.utc2.facilityui.model;

public class User {
    private String id;
    private String userId;
    private String username;
    private String email;
    private String avatar;
//
    public User(String id, String userId, String username, String email, String avatar) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
