package com.utc2.facility.dto.request;

import com.utc2.facility.enums.EquipmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentItemUpdateRequest {

    @Size(max = 255, message = "Mã tài sản không được vượt quá 255 ký tự")
    String assetTag;

    EquipmentStatus status;

    String defaultRoomId;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    String notes;
}
