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

    public String getTitleFacility() {
        return titleFacility;
    }

    public void setTitleFacility(String titleFacility) {
        this.titleFacility = titleFacility;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getFacilityMan() {
        return facilityMan;
    }

    public void setFacilityMan(String facilityMan) {
        this.facilityMan = facilityMan;
    }

    public String getGroupDirector() {
        return groupDirector;
    }

    public void setGroupDirector(String groupDirector) {
        this.groupDirector = groupDirector;
    }

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
