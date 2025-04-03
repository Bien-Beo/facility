package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairEquipmentRequestCreationRequest {
    String equipmentId;

    String userId;

    @NotNull
    String description;
}
