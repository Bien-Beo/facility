package com.utc2.facility.dto.request;

import com.utc2.facility.enums.BorrowRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequestCreationRequest {
    String userId;
    String roomName;
    String reason;
    LocalDateTime borrowDate;
    LocalDateTime returnDate;

    @NotNull
    LocalDateTime expectedReturnDate;
    String status;
}
