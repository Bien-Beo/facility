package com.utc2.facilityui.model;

public class CardBooking {
    String nameBooking;
    String purposeBooking;
    String dateBooking;
    String timeBooking;
    String requestBooking;
    String statusBooking;
    public CardBooking(){}

    public CardBooking(String purposeBooking, String nameBooking, String dateBooking, String timeBooking, String requestBooking, String statusBooking) {
        this.purposeBooking = purposeBooking;
        this.nameBooking = nameBooking;
        this.dateBooking = dateBooking;
        this.timeBooking = timeBooking;
        this.requestBooking = requestBooking;
        this.statusBooking = statusBooking;
    }

    public String getPurposeBooking() {
        return purposeBooking;
    }

    public void setPurposeBooking(String purposeBooking) {
        this.purposeBooking = purposeBooking;
    }

    public String getNameBooking() {
        return nameBooking;
    }

    public void setNameBooking(String nameBooking) {
        this.nameBooking = nameBooking;
    }

    public String getDateBooking() {
        return dateBooking;
    }

    public void setDateBooking(String dateBooking) {
        this.dateBooking = dateBooking;
    }

    public String getTimeBooking() {
        return timeBooking;
    }

    public void setTimeBooking(String timeBooking) {
        this.timeBooking = timeBooking;
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
}
