package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentItemCreationRequest {
    @NotBlank
    String modelId;
    String serialNumber;
    String assetTag;
    @PastOrPresent
    LocalDate purchaseDate;
    LocalDate warrantyExpiryDate;
    String defaultRoomId;
    String notes;
}
