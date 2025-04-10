package com.utc2.facilityui.model;

// Dựa trên InfoEquipment và thêm ID, sửa convention
public class Equipment {
    private String id; // Thêm ID để định danh duy nhất
    private String name;
    private String imageUrl; // Đổi 'Image' thành 'imageUrl' cho đúng convention
    private String description;
    private String equipmentType; // Đổi 'typeEquipment' thành 'equipmentType' (hoặc giữ nguyên nếu API trả về vậy)
    private String status;
    private String facilityManagerName; // Đổi 'nameFManager' thành tên rõ nghĩa hơn
    private String createdAt; // Đổi 'dateCreate'
    private String updatedAt; // Đổi 'dateUpdate'
    // Thêm các trường khác từ API nếu cần

    // Constructors (optional)
    public Equipment() {}

    // --- Getters and Setters cho TẤT CẢ các trường ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFacilityManagerName() { return facilityManagerName; }
    public void setFacilityManagerName(String facilityManagerName) { this.facilityManagerName = facilityManagerName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

}