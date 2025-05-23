package com.utc2.facilityui.model; // Hoặc package phù hợp trong project client của bạn

public class CancelBookingRequestData {
    private String reason;

    public CancelBookingRequestData() {

    }

    public CancelBookingRequestData(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}