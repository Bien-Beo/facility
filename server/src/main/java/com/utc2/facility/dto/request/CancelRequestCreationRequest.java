package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CancelRequestCreationRequest {
    String borrowRequestId;

    String userId;

    @NotNull
    String reason;

    @NotNull
    String status;
}//
