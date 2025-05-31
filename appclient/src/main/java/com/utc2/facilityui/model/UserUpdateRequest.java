package com.utc2.facilityui.model;

public class UserUpdateRequest {
    private String username;
    private String fullName;
    private String roleName;
    // private String avatar; // Nếu bạn muốn cho phép cập nhật avatar

    // Constructor
    public UserUpdateRequest(String username, String fullName, String roleName) {
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
    }

    // Getters (cần thiết cho Gson để serialize thành JSON)
    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoleName() {
        return roleName;
    }
}
