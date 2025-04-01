package com.utc2.facility.dto.response;

import com.utc2.facility.entity.RepairEquipmentRequest;
import com.utc2.facility.entity.RepairRoomRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RepairEquipmentRequestResponse {
    String id;
    String equipmentName;
    String userName;
    String description;
    String status;
    LocalDateTime createdAt;

        public static RepairEquipmentRequestResponse fromEntity(RepairEquipmentRequest repairEquipmentRequest) {
        return RepairEquipmentRequestResponse.builder()
                .id(repairEquipmentRequest.getId())
                .equipmentName(repairEquipmentRequest.getEquipment() != null ? repairEquipmentRequest.getEquipment().getName() : null)
                .userName(repairEquipmentRequest.getUser() != null ? repairEquipmentRequest.getUser().getUsername() : null)
                .description(repairEquipmentRequest.getDescription())
                .status(repairEquipmentRequest.getStatus().name())
                .createdAt(repairEquipmentRequest.getCreatedAt())
                .build();
    }
}
