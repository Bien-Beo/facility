package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentCreationRequest {
    @NotNull
    String name;

    String description;

    String roomName;

    String status;

    String img;

    String slug;

    String equipmentTypeName;

    String equipmentManagerId;
}//
