package com.utc2.facilityui.model;

public class BookedEquipmentItem {
    private String itemId;
    private String equipmentModelName;
    private int quantity;

    // private String notes;
    // private boolean isDefaultEquipment;
    // private String serialNumber;
    // private String assetTag;

    public BookedEquipmentItem() {}

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getEquipmentModelName() {
        return equipmentModelName;
    }

    public void setEquipmentModelName(String equipmentModelName) {
        this.equipmentModelName = equipmentModelName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}