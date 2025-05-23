package com.utc2.facilityui.model;

import java.time.LocalDateTime;
// Các import khác nếu có

public class CardAcceptBooking {
    private String nameBooking;
    private String userName;
    private String purposeBooking;
    private String timeRangeDisplay;
    private String requestBooking;
    private String statusBooking;
    private String bookingId;
    private String equipmentsDisplay;
    private String approvedByUserName;
    private LocalDateTime actualCheckOutTime;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    public CardAcceptBooking() {}

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

    public String getApprovedByUserName() {
        return approvedByUserName;
    }

    public void setApprovedByUserName(String approvedByUserName) {
        this.approvedByUserName = approvedByUserName;
    }

    public LocalDateTime getActualCheckOutTime() {
        return actualCheckOutTime;
    }

    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) {
        this.actualCheckOutTime = actualCheckOutTime;
    }

    public LocalDateTime getActualCheckInTime() {
        return actualCheckInTime;
    }

    public void setActualCheckInTime(LocalDateTime actualCheckInTime) {
        this.actualCheckInTime = actualCheckInTime;
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
}