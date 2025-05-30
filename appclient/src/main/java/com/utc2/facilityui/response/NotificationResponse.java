package com.utc2.facilityui.response;

import java.time.LocalDateTime;

// KHÔNG CÒN @JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse {

    private String id;
    private String name; // Tên người dùng (user.getFullName())
    private String message;
    private String type; // Tên của NotificationType enum
    private String status; // Tên của NotificationStatus enum (UNREAD, READ)
    private String createdAt; // Server gửi dạng String, client sẽ parse nếu cần
    private String userId;
    private String roomId;
    private String bookingId;

    // Constructors
    public NotificationResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    // Tiện ích để parse createdAt sang LocalDateTime ở client nếu cần
    public LocalDateTime getCreatedAtLocalDateTime() {
        if (this.createdAt == null || this.createdAt.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(this.createdAt);
        } catch (Exception e) {
            System.err.println("Error parsing createdAt string: " + this.createdAt + " - " + e.getMessage());
            return null;
        }
    }

    public boolean isUnread() {
        return "UNREAD".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "NotificationResponseClient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                // ... các trường khác ...
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}