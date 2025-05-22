package com.utc2.facilityui.model;

import java.time.LocalDateTime;

public class Booking {
    private String id;
    private String userId;
    private String roomId;
    private String purpose;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private String status;
    private String approvedByUserId;
    private String cancellationReason;
    private String cancelledByUserId;
    private LocalDateTime createdAt;
    private String note;
    private LocalDateTime updatedAt;

    private boolean approvedNotified;
    private boolean borrowNotified;
    private boolean overdueNotified;
    private boolean returnNotified;

    // Constructors
    public Booking() {}

    public Booking(String id, String userId, String roomId, String purpose,
                   LocalDateTime plannedStartTime, LocalDateTime plannedEndTime,
                   LocalDateTime actualCheckInTime, LocalDateTime actualCheckOutTime,
                   String status, String approvedByUserId, String cancellationReason,
                   String cancelledByUserId, LocalDateTime createdAt, String note,
                   LocalDateTime updatedAt, boolean approvedNotified, boolean borrowNotified,
                   boolean overdueNotified, boolean returnNotified) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.purpose = purpose;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.actualCheckInTime = actualCheckInTime;
        this.actualCheckOutTime = actualCheckOutTime;
        this.status = status;
        this.approvedByUserId = approvedByUserId;
        this.cancellationReason = cancellationReason;
        this.cancelledByUserId = cancelledByUserId;
        this.createdAt = createdAt;
        this.note = note;
        this.updatedAt = updatedAt;
        this.approvedNotified = approvedNotified;
        this.borrowNotified = borrowNotified;
        this.overdueNotified = overdueNotified;
        this.returnNotified = returnNotified;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

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

    public String getApprovedByUserId() { return approvedByUserId; }
    public void setApprovedByUserId(String approvedByUserId) { this.approvedByUserId = approvedByUserId; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getCancelledByUserId() { return cancelledByUserId; }
    public void setCancelledByUserId(String cancelledByUserId) { this.cancelledByUserId = cancelledByUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isApprovedNotified() { return approvedNotified; }
    public void setApprovedNotified(boolean approvedNotified) { this.approvedNotified = approvedNotified; }

    public boolean isBorrowNotified() { return borrowNotified; }
    public void setBorrowNotified(boolean borrowNotified) { this.borrowNotified = borrowNotified; }

    public boolean isOverdueNotified() { return overdueNotified; }
    public void setOverdueNotified(boolean overdueNotified) { this.overdueNotified = overdueNotified; }

    public boolean isReturnNotified() { return returnNotified; }
    public void setReturnNotified(boolean returnNotified) { this.returnNotified = returnNotified; }
}

