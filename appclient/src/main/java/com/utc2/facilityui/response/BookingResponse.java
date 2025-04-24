package com.utc2.facilityui.response; // Hoặc package dto.response

import java.util.List; // Nếu có danh sách equipment trả về

public class BookingResponse {
    private String id;
    private String roomId; // Hoặc facilityId
    private String roomName; // Thêm nếu API trả về
    private String purpose;
    private String plannedStartTime;
    private String plannedEndTime;
    private String actualStartTime;
    private String actualEndTime;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
    private String userId; // ID người đặt
    private String userName; // Thêm nếu API trả về
    private String createdAt;
    private String note;
    // private List<EquipmentResponse> bookedEquipments; // Nếu API trả về thiết bị đã đặt//

    // Getters and Setters (Quan trọng!)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getPlannedStartTime() { return plannedStartTime; }
    public void setPlannedStartTime(String plannedStartTime) { this.plannedStartTime = plannedStartTime; }
    public String getPlannedEndTime() { return plannedEndTime; }
    public void setPlannedEndTime(String plannedEndTime) { this.plannedEndTime = plannedEndTime; }
    public String getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(String actualStartTime) { this.actualStartTime = actualStartTime; }
    public String getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(String actualEndTime) { this.actualEndTime = actualEndTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    // public List<EquipmentResponse> getBookedEquipments() { return bookedEquipments; }
    // public void setBookedEquipments(List<EquipmentResponse> bookedEquipments) { this.bookedEquipments = bookedEquipments; }
}