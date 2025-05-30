package com.utc2.facilityui.model;

public class CardBooking { // Dành cho PENDING_APPROVAL
    private String nameBooking;     // Tên phòng
    private String userName;        // << MỚI: Tên người đặt
    private String purposeBooking;
    private String timeRangeDisplay; // << MỚI: "HH:mm, dd/MM/yyyy - HH:mm" hoặc "HH:mm - HH:mm, dd/MM/yyyy"
    private String requestBooking;  // Thời điểm yêu cầu (đã định dạng)
    private String statusBooking;   // Trạng thái (sẽ là "Chờ duyệt")
    private String bookingId;
    private String equipmentsDisplay;
    private String cancellationReason;
    public CardBooking() {}

    public String getNameBooking() {
        return nameBooking;
    }

    public void setNameBooking(String nameBooking) {
        this.nameBooking = nameBooking;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPurposeBooking() {
        return purposeBooking;
    }

    public void setPurposeBooking(String purposeBooking) {
        this.purposeBooking = purposeBooking;
    }

    public String getTimeRangeDisplay() {
        return timeRangeDisplay;
    }

    public void setTimeRangeDisplay(String timeRangeDisplay) {
        this.timeRangeDisplay = timeRangeDisplay;
    }

    public String getRequestBooking() {
        return requestBooking;
    }

    public void setRequestBooking(String requestBooking) {
        this.requestBooking = requestBooking;
    }

    public String getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(String statusBooking) {
        this.statusBooking = statusBooking;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getEquipmentsDisplay() {
        return equipmentsDisplay;
    }

    public void setEquipmentsDisplay(String equipmentsDisplay) {
        this.equipmentsDisplay = equipmentsDisplay;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}