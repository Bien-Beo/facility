package com.utc2.facility.dto.response;

import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.enums.EquipmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentResponse {
    String id;
    String modelName;
    String typeName;
    String serialNumber;
    EquipmentStatus status;
    LocalDate purchaseDate;
    LocalDate warrantyExpiryDate;
    String defaultRoomName;
    String notes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static EquipmentResponse fromEntity(EquipmentItem equipmentItem) {
        return EquipmentResponse.builder()
                .id(equipmentItem.getId())
                .status(equipmentItem.getStatus())
                .createdAt(equipmentItem.getCreatedAt())
                .updatedAt(equipmentItem.getUpdatedAt())
                .modelName(equipmentItem.getModel().getName())
                .serialNumber(equipmentItem.getSerialNumber())
                .purchaseDate(equipmentItem.getPurchaseDate())
                .warrantyExpiryDate(equipmentItem.getWarrantyExpiryDate())
                .defaultRoomName(equipmentItem.getDefaultRoom() != null ? equipmentItem.getDefaultRoom().getName() : null)
                .notes(equipmentItem.getNotes())
                .typeName(equipmentItem.getModel().getEquipmentType().getName())
                .build();
    }
}
