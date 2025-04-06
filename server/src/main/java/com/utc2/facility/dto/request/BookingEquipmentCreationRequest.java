package com.utc2.facility.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingEquipmentCreationRequest {
    String bookingId;
    String equipmentId;
    Boolean isDefaultEquipment = false;
    String notes;
}
