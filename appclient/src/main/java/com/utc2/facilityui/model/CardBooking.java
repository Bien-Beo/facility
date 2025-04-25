package com.utc2.facilityui.model;

// Lớp mô hình dữ liệu cho CardBooking, chứa các chuỗi đã được chuẩn bị để hiển thị
public class CardBooking {
    private String nameBooking;
    private String purposeBooking;
    private String plannedStartTimeDisplay;
    private String plannedEndTimeDisplay;
    private String requestBooking;
    private String statusBooking;
    private String bookingId;         

    // Constructor
    public CardBooking() {}

    // Getters and Setters
    public String getNameBooking() { return nameBooking; }
    public void setNameBooking(String nameBooking) { this.nameBooking = nameBooking; }

    public String getPurposeBooking() { return purposeBooking; }
    public void setPurposeBooking(String purposeBooking) { this.purposeBooking = purposeBooking; }

    public String getPlannedStartTimeDisplay() { return plannedStartTimeDisplay; }
    public void setPlannedStartTimeDisplay(String plannedStartTimeDisplay) { this.plannedStartTimeDisplay = plannedStartTimeDisplay; }

    public String getPlannedEndTimeDisplay() { return plannedEndTimeDisplay; }
    public void setPlannedEndTimeDisplay(String plannedEndTimeDisplay) { this.plannedEndTimeDisplay = plannedEndTimeDisplay; }

    public String getRequestBooking() { return requestBooking; }
    public void setRequestBooking(String requestBooking) { this.requestBooking = requestBooking; }

    public String getStatusBooking() { return statusBooking; }
    public void setStatusBooking(String statusBooking) { this.statusBooking = statusBooking; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
}