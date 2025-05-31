package com.utc2.facilityui.model;

public class UserCreationRequest {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String avatar; // Trường này có thể là null nếu không bắt buộc
    private String roleName;

    // Constructor để dễ dàng tạo đối tượng
    public UserCreationRequest(String userId, String username, String email, String password, String fullName, String avatar, String roleName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.roleName = roleName;
    }

    // Getters (không bắt buộc phải có setters nếu bạn chỉ tạo đối tượng một lần và không thay đổi)
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRoleName() {
        return roleName;
    }
}