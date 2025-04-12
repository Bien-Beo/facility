package com.utc2.facilityui.model;
//
import java.util.List;


public class BookingCreationRequest {
    private String roomId; // Hoặc facilityId nếu có thể đặt cả phòng và thiết bị
    private String purpose;
    private String plannedStartTime;
    private String plannedEndTime;
    private List<String> additionalEquipmentItemIds; // Danh sách ID thiết bị mượn kèm
    private String note;

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getPlannedStartTime() { return plannedStartTime; }
    public void setPlannedStartTime(String plannedStartTime) { this.plannedStartTime = plannedStartTime; }
    public String getPlannedEndTime() { return plannedEndTime; }
    public void setPlannedEndTime(String plannedEndTime) { this.plannedEndTime = plannedEndTime; }
    public List<String> getAdditionalEquipmentItemIds() { return additionalEquipmentItemIds; }
    public void setAdditionalEquipmentItemIds(List<String> additionalEquipmentItemIds) { this.additionalEquipmentItemIds = additionalEquipmentItemIds; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}