package com.utc2.facility.dto.request;

import com.utc2.facility.enums.EquipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentItemUpdateRequest {
    String assetTag;
    EquipmentStatus status;
    String defaultRoomId;
    String notes;
}
