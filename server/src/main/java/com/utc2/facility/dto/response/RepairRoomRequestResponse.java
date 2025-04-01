package com.utc2.facility.dto.response;

import com.utc2.facility.entity.CancelRequest;
import com.utc2.facility.entity.RepairRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RepairRequestResponse {
    String id;
    String roomName;
    String equipmentName;
    String userName;
    String description;
    Boolean isRoomIssue;
    String status;
    LocalDateTime createdAt;

        public static RepairRequestResponse fromEntity(RepairRequest repairRequest) {
        return RepairRequestResponse.builder()
                .id(repairRequest.getId())
                .roomName(repairRequest.getRoom() != null ? repairRequest.getRoom().getName() : null)
                .equipmentName(repairRequest.getEquipment() != null ? repairRequest.getEquipment().getName() : null)
                .userName(repairRequest.getUser() != null ? repairRequest.getUser().getUsername() : null)
                .description(repairRequest.getDescription())
                .isRoomIssue(repairRequest.getIsRoomIssue())
                .status(repairRequest.getStatus().name())
                .createdAt(repairRequest.getCreatedAt())
                .build();
    }
}
