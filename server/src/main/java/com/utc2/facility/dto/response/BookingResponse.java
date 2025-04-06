package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Booking;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestResponse {
    String id;
    String userName;
    String roomName;
    String reason;
    LocalDateTime borrowDate;
    LocalDateTime returnDate;
    LocalDateTime expectedReturnDate;
    String status;

    public static BorrowRequestResponse fromEntity(Booking booking) {
        return BorrowRequestResponse.builder()
                .id(booking.getId())
                .userName(booking.getUser() != null ? booking.getUser().getUsername() : null)
                .roomName(booking.getRoom() != null ? booking.getRoom().getName() : null)
                .reason(booking.getReason())
                .borrowDate(booking.getBorrowDate())
                .returnDate(booking.getReturnDate())
                .expectedReturnDate(booking.getExpectedReturnDate())
                .status(booking.getStatus().name())
                .build();
    }
}//
