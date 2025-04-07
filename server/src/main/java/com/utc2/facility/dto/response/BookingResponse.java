package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    String id;
    String userName;
    String roomName;
    String purpose;
    LocalDateTime plannedStartTime;
    LocalDateTime plannedEndTime;
    LocalDateTime actualCheckInTime;
    LocalDateTime actualCheckOutTime;
    String status;
    String approvedByUserName;
    String cancellationReason;
    String cancelledByUserName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String note;
    List<BookingEquipmentResponse> bookedEquipments;


    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userName(booking.getUser() != null ? booking.getUser().getUsername() : null)
                .roomName(booking.getRoom() != null ? booking.getRoom().getName() : null)
                .purpose(booking.getPurpose())
                .plannedStartTime(booking.getPlannedStartTime())
                .plannedEndTime(booking.getPlannedEndTime())
                .actualCheckInTime(booking.getActualCheckInTime())
                .actualCheckOutTime(booking.getActualCheckOutTime())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .approvedByUserName(booking.getApprovedByUser() != null ? booking.getApprovedByUser().getUsername() : null)
                .cancellationReason(booking.getCancellationReason())
                .cancelledByUserName(booking.getCancelledByUser() != null ? booking.getCancelledByUser().getUsername() : null)
                .note(booking.getNote())
                .status(booking.getStatus().name())
                .build();
    }
}
