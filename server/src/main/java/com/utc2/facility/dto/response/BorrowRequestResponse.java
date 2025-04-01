package com.utc2.facility.dto.response;

import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.Equipment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

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

    public static BorrowRequestResponse fromEntity(BorrowRequest borrowRequest) {
        return BorrowRequestResponse.builder()
                .id(borrowRequest.getId())
                .userName(borrowRequest.getUser() != null ? borrowRequest.getUser().getUsername() : null)
                .roomName(borrowRequest.getRoom() != null ? borrowRequest.getRoom().getName() : null)
                .reason(borrowRequest.getReason())
                .borrowDate(borrowRequest.getBorrowDate())
                .returnDate(borrowRequest.getReturnDate())
                .expectedReturnDate(borrowRequest.getExpectedReturnDate())
                .status(borrowRequest.getStatus().name())
                .build();
    }
}
