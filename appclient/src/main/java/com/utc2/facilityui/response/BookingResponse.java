package com.utc2.facilityui.response;

import com.utc2.facilityui.model.BookedEquipmentItem;
import com.utc2.facilityui.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
// import java.util.List; // Bỏ comment nếu API trả về bookedEquipments

public class BookingResponse {
    private String id;
    private String userName;
    private String roomName;
    private String purpose;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private String status;
    private String approvedByUserName;
    private String cancellationReason; // Thêm nếu API trả về
    private String cancelledByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
    private List<BookedEquipmentItem> bookedEquipments; // << ĐÃ THÊM
    private List<String> equipmentItemIds = new ArrayList<>();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public LocalDateTime getActualCheckInTime() {
        return actualCheckInTime;
    }

    public void setActualCheckInTime(LocalDateTime actualCheckInTime) {
        this.actualCheckInTime = actualCheckInTime;
    }

    public LocalDateTime getActualCheckOutTime() {
        return actualCheckOutTime;
    }

    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) {
        this.actualCheckOutTime = actualCheckOutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovedByUserName() {
        return approvedByUserName;
    }

    public void setApprovedByUserName(String approvedByUserName) {
        this.approvedByUserName = approvedByUserName;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getCancelledByUserName() {
        return cancelledByUserName;
    }

    public void setCancelledByUserName(String cancelledByUserName) {
        this.cancelledByUserName = cancelledByUserName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<BookedEquipmentItem> getBookedEquipments() {
        return bookedEquipments;
    }

    public void setBookedEquipments(List<BookedEquipmentItem> bookedEquipments) {
        this.bookedEquipments = bookedEquipments;
    }

    public List<String> getEquipmentItemIds() {
        return equipmentItemIds;
    }

    public void setEquipmentItemIds(List<String> equipmentItemIds) {
        this.equipmentItemIds = equipmentItemIds;
    }
}