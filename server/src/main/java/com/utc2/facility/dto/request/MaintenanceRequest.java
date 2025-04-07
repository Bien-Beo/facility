package com.utc2.facility.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaintenanceRequest {

    String roomId;
    String itemId;

    @NotBlank(message = "Mô tả sự cố không được để trống")
    String description;

    @AssertTrue(message = "Vui lòng chỉ định ID phòng hoặc ID thiết bị, không phải cả hai hoặc không có.")
    private boolean isValidTarget() {
        boolean roomProvided = (roomId != null && !roomId.trim().isEmpty());
        boolean itemProvided = (itemId != null && !itemId.trim().isEmpty());
        return roomProvided ^ itemProvided; // Sử dụng XOR: true nếu chỉ một trong hai là true
    }
}
