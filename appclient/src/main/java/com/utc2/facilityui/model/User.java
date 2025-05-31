package com.utc2.facilityui.model;

import com.utc2.facilityui.response.UserResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class User {
    // Trường dữ liệu (lưu giá trị thô từ DTO)
    private String id;
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private String avatar;
    private String roleName;
    private String createdAt; // Lưu chuỗi ISO thô
    private String updatedAt; // Lưu chuỗi ISO thô

    // JavaFX Properties (transient)
    private transient StringProperty idProperty;
    private transient StringProperty userIdProperty;
    private transient StringProperty usernameProperty; // Thêm nếu có cột username
    private transient StringProperty fullNameProperty;
    private transient StringProperty emailProperty;
    private transient StringProperty roleNameProperty;
    private transient StringProperty createdAtProperty;
    private transient StringProperty updatedAtProperty;

    private static final String NOT_AVAILABLE = "N/A";
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy", new Locale("vi", "VN"));

    public User() {}

    public User(UserResponse dto) {
        if (dto == null) return;
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.username = dto.getUsername();
        this.fullName = dto.getFullName();
        this.email = dto.getEmail();
        this.avatar = dto.getAvatar();
        this.roleName = dto.getRoleName();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }

    private String formatDisplayDateTime(String isoDateTimeString) {
        if (isoDateTimeString == null || isoDateTimeString.isBlank() || isoDateTimeString.equalsIgnoreCase("null")) {
            return NOT_AVAILABLE;
        }
        LocalDateTime dateTime;
        try {
            Instant instant = Instant.parse(isoDateTimeString);
            dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e1) {
            try {
                dateTime = LocalDateTime.parse(isoDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                try {
                    dateTime = LocalDateTime.parse(isoDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } catch (DateTimeParseException e3) {
                    System.err.println("User Model: Lỗi parse ngày '" + isoDateTimeString + "': " + e3.getMessage());
                    return NOT_AVAILABLE + " (lỗi)";
                }
            }
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }

    private String getFormattedString(String value) {
        return (value == null || value.isBlank()) ? NOT_AVAILABLE : value;
    }

    // --- Property Getters (PHẢI LÀ PUBLIC) ---
    public StringProperty idProperty() {
        if (idProperty == null) idProperty = new SimpleStringProperty(this, "id", getId());
        return idProperty;
    }
    public StringProperty userIdProperty() {
        if (userIdProperty == null) userIdProperty = new SimpleStringProperty(this, "userId", getUserId());
        return userIdProperty;
    }
    public StringProperty usernameProperty() { // Thêm property cho username
        if (usernameProperty == null) usernameProperty = new SimpleStringProperty(this, "username", getUsername());
        return usernameProperty;
    }
    public StringProperty fullNameProperty() {
        if (fullNameProperty == null) fullNameProperty = new SimpleStringProperty(this, "fullName", getFullName());
        return fullNameProperty;
    }
    public StringProperty emailProperty() {
        if (emailProperty == null) emailProperty = new SimpleStringProperty(this, "email", getEmail());
        return emailProperty;
    }
    public StringProperty roleNameProperty() {
        if (roleNameProperty == null) roleNameProperty = new SimpleStringProperty(this, "roleName", getRoleName());
        return roleNameProperty;
    }
    public StringProperty createdAtProperty() {
        if (createdAtProperty == null) createdAtProperty = new SimpleStringProperty(this, "createdAt", getCreatedAt());
        return createdAtProperty;
    }
    public StringProperty updatedAtProperty() {
        if (updatedAtProperty == null) updatedAtProperty = new SimpleStringProperty(this, "updatedAt", getUpdatedAt());
        return updatedAtProperty;
    }

    // --- Standard Getters (PHẢI LÀ PUBLIC) ---
    public String getId() { return getFormattedString(this.id); }
    public String getUserId() { return getFormattedString(this.userId); }
    public String getUsername() { return getFormattedString(this.username); }
    public String getFullName() { return getFormattedString(this.fullName); }
    public String getEmail() { return getFormattedString(this.email); }
    public String getAvatar() { return this.avatar; } // Có thể null
    public String getRoleName() { return getFormattedString(this.roleName); }
    public String getCreatedAt() { return formatDisplayDateTime(this.createdAt); }
    public String getUpdatedAt() { return formatDisplayDateTime(this.updatedAt); }

    // --- Setters (PHẢI LÀ PUBLIC nếu bạn set giá trị từ bên ngoài) ---
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}