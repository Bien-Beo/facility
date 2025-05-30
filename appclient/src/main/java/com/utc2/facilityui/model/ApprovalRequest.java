package com.utc2.facilityui.model;

import java.time.LocalDateTime;

public class ApprovalRequest {
    private String bookingId;
    private String facilityName;
    private String purpose;
    private String formattedDate;        // Ví dụ: "Thứ Tư, 07/05/2025"
    private String timeRange;            // Ví dụ: "07:00 - 11:30"
    private String requestedBy;
    private String formattedRequestedAt; // Ví dụ: "12:00, Thứ Ba 06/05/2025"
    private String statusDisplay;        // Ví dụ: "Chờ duyệt"
    private String statusKey;            // Ví dụ: "PENDING_APPROVAL"

    private LocalDateTime plannedStartTimeOriginal;
    private LocalDateTime requestedAtOriginal;

    public ApprovalRequest(String bookingId, String facilityName, String purpose,
                           String formattedDate, String timeRange, String requestedBy,
                           String formattedRequestedAt, String statusDisplay, String statusKey,
                           LocalDateTime plannedStartTimeOriginal, LocalDateTime requestedAtOriginal) {
        this.bookingId = bookingId;
        this.facilityName = facilityName;
        this.purpose = purpose;
        this.formattedDate = formattedDate;
        this.timeRange = timeRange;
        this.requestedBy = requestedBy;
        this.formattedRequestedAt = formattedRequestedAt;
        this.statusDisplay = statusDisplay;
        this.statusKey = statusKey;
        this.plannedStartTimeOriginal = plannedStartTimeOriginal;
        this.requestedAtOriginal = requestedAtOriginal;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getFacilityName() { return facilityName; }
    public String getPurpose() { return purpose; }
    public String getFormattedDate() { return formattedDate; }
    public String getTimeRange() { return timeRange; }
    public String getRequestedBy() { return requestedBy; }
    public String getFormattedRequestedAt() { return formattedRequestedAt; }
    public String getStatusDisplay() { return statusDisplay; }
    public String getStatusKey() { return statusKey; }
    public LocalDateTime getPlannedStartTimeOriginal() { return plannedStartTimeOriginal; }
    public LocalDateTime getRequestedAtOriginal() { return requestedAtOriginal; }
}