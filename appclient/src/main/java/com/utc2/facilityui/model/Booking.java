package com.utc2.facilityui.model;

import java.time.LocalDateTime;

public class Booking {
    private String titleFacility;
    private String requestedBy;
    private String purpose;
    private LocalDateTime date;
    private String timeSlot;
    private LocalDateTime requestedAt;
    private String groupDirector;
    private String facilityMan;

    // Constructor
    public Booking(String titleFacility, String requestedBy, String purpose, LocalDateTime date, String timeSlot, LocalDateTime requestedAt, String groupDirector, String facilityMan) {
        this.titleFacility = titleFacility;
        this.requestedBy = requestedBy;
        this.purpose = purpose;
        this.date = date;
        this.timeSlot = timeSlot;
        this.requestedAt = requestedAt;
        this.groupDirector = groupDirector;
        this.facilityMan = facilityMan;
    }

    // Getters cho tất cả các thuộc tính

    public String getTitleFacility() { return titleFacility; }
    public String getRequestedBy() { return requestedBy; }
    public String getPurpose() { return purpose; }
    public LocalDateTime getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public String getGroupDirector() { return groupDirector; }
    public String getFacilityMan() { return facilityMan; }

    // Setters (nếu cần)
    public void setTitleFacility(String titleFacility) { this.titleFacility = titleFacility; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public void getGroupDirector(String groupDirector) { this.groupDirector = groupDirector; }
    public void getFacilityMan(String facilityMan) { this.facilityMan = facilityMan; }

    @Override
    public String toString() {
        return "Booking{" +
                "titleFacility='" + titleFacility + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", purpose='" + purpose + '\'' +
                ", date=" + date +
                ", timeSlot='" + timeSlot + '\'' +
                ", requestedAt=" + requestedAt +
                ", groupDirector='" + groupDirector + '\'' +
                ", facilityMan='" + facilityMan + '\'' +
                '}';
    }
}