package com.utc2.facilityui.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CancellationRequest {
    private String titleFacility;
    private String purpose;
    private LocalDateTime date;
    private String timeRange;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime cancellationRequestedAt;
    private String cancellationRemark;

    public CancellationRequest(String titleFacility, String purpose, LocalDateTime date, String timeRange, String requestedBy, LocalDateTime requestedAt, LocalDateTime cancellationRequestedAt, String cancellationRemark) {
        this.titleFacility = titleFacility;
        this.purpose = purpose;
        this.date = date;
        this.timeRange = timeRange;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
        this.cancellationRequestedAt = cancellationRequestedAt;
        this.cancellationRemark = cancellationRemark;
    }

    public String getTitleFacility() { return titleFacility; }
    public String getPurpose() { return purpose; }
    public LocalDateTime getDate() { return date; }
    public String getTimeRange() { return timeRange; }
    public String getRequestedBy() { return requestedBy; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getCancellationRequestedAt() { return cancellationRequestedAt; }
    public String getCancellationRemark() { return cancellationRemark; }

    public String getFormattedDate() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy")) : "";
    }

    public String getFormattedRequestedAt() {
        return requestedAt != null ? requestedAt.format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mm a")) : "";
    }

    public String getFormattedCancellationRequestedAt() {
        return cancellationRequestedAt != null ? cancellationRequestedAt.format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mm a")) : "";
    }
}
