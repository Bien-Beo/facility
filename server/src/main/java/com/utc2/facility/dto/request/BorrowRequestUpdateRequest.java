package com.utc2.facility.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequestUpdateRequest {
    String userId;
    String roomName;
    String reason;
    LocalDateTime borrowDate;
    LocalDateTime returnDate;
    LocalDateTime expectedReturnDate;
    String status;
}