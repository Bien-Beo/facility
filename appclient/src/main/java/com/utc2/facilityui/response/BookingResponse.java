package com.utc2.facilityui.response; // Hoặc package tương ứng trong UI project

import java.time.LocalDateTime;
import java.util.List;

// Nên khớp hoàn toàn với cấu trúc JSON trả về từ API
// Nếu dùng thư viện như Jackson, tên trường cần khớp key trong JSON
public class BookingResponse {
    private String id;
    private String userName; // Tên người dùng đặt phòng
    private String roomName; // Tên phòng
    private String purpose; // Mục đích
    private LocalDateTime plannedStartTime; // Thời gian bắt đầu dự kiến
    private LocalDateTime plannedEndTime; // Thời gian kết thúc dự kiến
    private LocalDateTime actualCheckInTime; // Thời gian check-in thực tế (có thể null)
    private LocalDateTime actualCheckOutTime; // Thời gian check-out thực tế (có thể null)
    private String status; // Trạng thái (PENDING, APPROVED, CANCELLED, etc.)
    private String approvedByUserName; // Tên người duyệt (có thể null)
    private String cancellationReason; // Lý do hủy (có thể null)
    private String cancelledByUserName; // Tên người hủy (có thể null)
    private LocalDateTime createdAt; // Thời gian tạo yêu cầu
    private LocalDateTime updatedAt; // Thời gian cập nhật cuối (có thể null)
    private String note; // Ghi chú (có thể null)
    // Giả sử API không trả về bookedEquipments trong danh sách chính, nếu có thì thêm vào
    // private List<SomeEquipmentResponse> bookedEquipments;

    // --- Getters and Setters ---
    // (Bắt buộc phải có để thư viện như Jackson hoạt động)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
    public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }

    public LocalDateTime getPlannedEndTime() { return plannedEndTime; }
    public void setPlannedEndTime(LocalDateTime plannedEndTime) { this.plannedEndTime = plannedEndTime; }

    public LocalDateTime getActualCheckInTime() { return actualCheckInTime; }
    public void setActualCheckInTime(LocalDateTime actualCheckInTime) { this.actualCheckInTime = actualCheckInTime; }

    public LocalDateTime getActualCheckOutTime() { return actualCheckOutTime; }
    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) { this.actualCheckOutTime = actualCheckOutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedByUserName() { return approvedByUserName; }
    public void setApprovedByUserName(String approvedByUserName) { this.approvedByUserName = approvedByUserName; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getCancelledByUserName() { return cancelledByUserName; }
    public void setCancelledByUserName(String cancelledByUserName) { this.cancelledByUserName = cancelledByUserName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // public List<SomeEquipmentResponse> getBookedEquipments() { return bookedEquipments; }
    // public void setBookedEquipments(List<SomeEquipmentResponse> bookedEquipments) { this.bookedEquipments = bookedEquipments; }

    // toString() (Tùy chọn, hữu ích cho debugging)
    @Override
    public String toString() {
        return "BookingResponse{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", roomName='" + roomName + '\'' +
                ", status='" + status + '\'' +
                ", plannedStartTime=" + plannedStartTime +
                ", createdAt=" + createdAt +
                '}';
    }
}