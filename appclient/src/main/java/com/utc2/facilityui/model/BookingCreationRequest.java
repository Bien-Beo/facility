package com.utc2.facilityui.model;

import java.time.LocalDateTime;
import java.util.List;

public class BookingCreationRequest {
    private String roomId;
    private String purpose;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private List<String> additionalEquipmentItemIds;
    private String note;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public java.util.List<String> getAdditionalEquipmentItemIds() {
        return additionalEquipmentItemIds;
    }

    public void setAdditionalEquipmentItemIds(java.util.List<String> additionalEquipmentItemIds) {
        this.additionalEquipmentItemIds = additionalEquipmentItemIds;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
