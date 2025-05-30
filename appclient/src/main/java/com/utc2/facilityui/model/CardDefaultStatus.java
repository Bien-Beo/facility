package com.utc2.facilityui.model;

public class CardDefaultStatus {
    private String nameBooking;
    private String userName;
    private String purposeBooking;
    private String timeRangeDisplay;    // << ĐÃ CÓ
    private String requestBooking;
    private String statusBooking;
    private String bookingId;
    private String equipmentsDisplay;

    public CardDefaultStatus() {}

    // Getters and Setters (tương tự như CardAcceptBooking)
    public String getNameBooking() { return nameBooking; }
    public void setNameBooking(String nameBooking) { this.nameBooking = nameBooking; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPurposeBooking() { return purposeBooking; }
    public void setPurposeBooking(String purposeBooking) { this.purposeBooking = purposeBooking; }

    public String getTimeRangeDisplay() { return timeRangeDisplay; }
    public void setTimeRangeDisplay(String timeRangeDisplay) { this.timeRangeDisplay = timeRangeDisplay; }

    public String getRequestBooking() { return requestBooking; }
    public void setRequestBooking(String requestBooking) { this.requestBooking = requestBooking; }

    public String getStatusBooking() { return statusBooking; }
    public void setStatusBooking(String statusBooking) { this.statusBooking = statusBooking; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getEquipmentsDisplay() { return equipmentsDisplay; }
    public void setEquipmentsDisplay(String equipmentsDisplay) { this.equipmentsDisplay = equipmentsDisplay; }
}