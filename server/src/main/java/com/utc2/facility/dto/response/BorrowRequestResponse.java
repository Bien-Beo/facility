package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Equipment;
import com.utc2.facility.entity.Room;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentResponse {
    String id;
    String name;
    String description;
    String slug;
    String img;
    String status;
    String roomName;
    String equipmentTypeName;
    String nameFacilityManager;
    Boolean isActive;
    Date createdAt;
    Date updatedAt;
    Date deletedAt;

    public static EquipmentResponse fromEntity(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .description(equipment.getDescription())
                .slug(equipment.getSlug())
                .img(equipment.getImg())
                .status(equipment.getStatus().name())
                .roomName(equipment.getRoom() != null ? equipment.getRoom().getName() : null)
                .equipmentTypeName(equipment.getEquipmentType() != null ? equipment.getEquipmentType().getName() : null)
                .nameFacilityManager(equipment.getRoom() != null && equipment.getRoom().getFacilityManager() != null ? equipment.getRoom().getFacilityManager().getUsername() : null)
                .isActive(equipment.getIsActive())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .deletedAt(equipment.getDeletedAt())
                .build();
    }
}
