package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentItemCreationRequest {
    @NotBlank(message = "ID Model không được để trống")
    String modelId;

    @Size(max = 255, message = "Số seri không được vượt quá 255 ký tự")
    String serialNumber;

    @Size(max = 255, message = "Mã tài sản không được vượt quá 255 ký tự")
    String assetTag;

    @PastOrPresent(message = "Ngày mua phải là ngày trong quá khứ hoặc hiện tại")
    LocalDate purchaseDate;

    LocalDate warrantyExpiryDate;

    String defaultRoomId;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    String notes;
}
