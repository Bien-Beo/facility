package com.utc2.facility.dto.response;

import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import com.utc2.facility.enums.EquipmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentModelResponse {
    String id;
    String name;
    String equipmentTypeName;
    String manufacturer;
    String description;
    String specifications;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String imageUrl;

    public static EquipmentModelResponse fromEntity(EquipmentModel equipmentModel) {
        return EquipmentModelResponse.builder()
                .id(equipmentModel.getId())
                .name(equipmentModel.getName())
                .createdAt(equipmentModel.getCreatedAt())
                .updatedAt(equipmentModel.getUpdatedAt())
                .equipmentTypeName(equipmentModel.getEquipmentType().getName())
                .manufacturer(equipmentModel.getManufacturer())
                .description(equipmentModel.getDescription())
                .specifications(equipmentModel.getSpecifications())
                .imageUrl(equipmentModel.getImageUrl())
                .build();
    }
}
