package com.utc2.facilityui.response;

public class EquipmentResponse {
    private String bookingId;
    private String itemId;
    private boolean isDefaultEquipment;
    private String note;

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public boolean isDefaultEquipment() { return isDefaultEquipment; }
    public void setDefaultEquipment(boolean defaultEquipment) { isDefaultEquipment = defaultEquipment; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}


