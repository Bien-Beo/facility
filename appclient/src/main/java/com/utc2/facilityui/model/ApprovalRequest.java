package com.utc2.facilityui.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApprovalRequest {
    private String facilityName;
    private String purpose;
    private LocalDateTime date;
    private String timeRange;
    private String requestedBy;
    private LocalDateTime requestedAt;

    public ApprovalRequest(String facilityName, String purpose, LocalDateTime date, String timeRange, String requestedBy, LocalDateTime requestedAt) {
        this.facilityName = facilityName;
        this.purpose = purpose;
        this.date = date;
        this.timeRange = timeRange;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getFormattedDate() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
        return "";
    }

    public String getFormattedRequestedAt() {
        if (requestedAt != null) {
            return requestedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
        }
        return "";
    }

    @Override
    public String toString() {
        return "ApprovalRequest{" +
                "facilityName='" + facilityName + '\'' +
                ", purpose='" + purpose + '\'' +
                ", date=" + date +
                ", timeRange='" + timeRange + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", requestedAt=" + requestedAt +
                '}';
    }
}