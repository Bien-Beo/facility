package com.utc2.facilityui.model;

// Model này có thể dựa trên UserResponse từ server
public class UserItem {
    private String id; // UUID của User
    private String fullName; // Tên đầy đủ để hiển thị
    private String username; // Có thể dùng username nếu fullName không có

    public UserItem(String id, String fullName, String username) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
    }

    public UserItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter để hiển thị trong ComboBox (ưu tiên fullName)
    public String getDisplayName() {
        return (fullName != null && !fullName.isEmpty()) ? fullName : username;
    }
}