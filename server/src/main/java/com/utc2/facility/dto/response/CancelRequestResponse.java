package com.utc2.facility.dto.response;

import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.CancelRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequestResponse {
    String id;
    String borrowRequestId;
    String userName;
    String reason;
    String status;
    LocalDateTime createdAt;

    public static CancelRequestResponse fromEntity(CancelRequest cancelRequest) {
        return CancelRequestResponse.builder()
                .id(cancelRequest.getId())
                .borrowRequestId(cancelRequest.getBorrowRequest().getId())
                .userName(cancelRequest.getUser().getUsername())
                .reason(cancelRequest.getReason())
                .status(cancelRequest.getStatus().name())
                .createdAt(cancelRequest.getCreatedAt())
                .build();
    }
}
