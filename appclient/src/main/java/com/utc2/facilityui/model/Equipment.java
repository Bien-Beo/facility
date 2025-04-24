package com.utc2.facilityui.model;
//
public class Equipment {
    private String id;

    private String modelName; // Khớp với JSON

    private String typeName;

    private String imgModel; // Khớp với JSON

    private String notes;

    private String status;


    private String serialNumber;
    private String purchaseDate;
    private String warrantyExpiryDate;
    private String defaultRoomName; // Có thể hữu ích

    private String createdAt; // Giữ nguyên vì khớp
    private String updatedAt; // Giữ nguyên vì khớp


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getTypeName() { return typeName; } // <<< SỬA Ở ĐÂY
    public void setTypeName(String typeName) { this.typeName = typeName; } // <<< SỬA Ở ĐÂY

    public String getImgModel() { return imgModel; }
    public void setImgModel(String imgModel) { this.imgModel = imgModel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(String warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }

    public String getDefaultRoomName() { return defaultRoomName; }
    public void setDefaultRoomName(String defaultRoomName) { this.defaultRoomName = defaultRoomName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // public String getFacilityManagerName() { return facilityManagerName; }
    // public void setFacilityManagerName(String facilityManagerName) { this.facilityManagerName = facilityManagerName; }

}