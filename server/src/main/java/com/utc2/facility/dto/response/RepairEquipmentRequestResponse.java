package com.utc2.facility.dto.response;

import com.utc2.facility.entity.RepairRoomRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RepairRoomRequestResponse {
    String id;
    String roomName;
    String userName;
    String description;
    String status;
    LocalDateTime createdAt;

        public static RepairRoomRequestResponse fromEntity(RepairRoomRequest repairRoomRequest) {
        return RepairRoomRequestResponse.builder()
                .id(repairRoomRequest.getId())
                .roomName(repairRoomRequest.getRoom() != null ? repairRoomRequest.getRoom().getName() : null)
                .userName(repairRoomRequest.getUser() != null ? repairRoomRequest.getUser().getUsername() : null)
                .description(repairRoomRequest.getDescription())
                .status(repairRoomRequest.getStatus().name())
                .createdAt(repairRoomRequest.getCreatedAt())
                .build();
    }
}
