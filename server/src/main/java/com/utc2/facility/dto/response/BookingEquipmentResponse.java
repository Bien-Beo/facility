package com.utc2.facility.dto.response;

import com.utc2.facility.entity.BookingEquipment;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BookingEquipmentResponse {
    String itemId;
    String equipmentModelName;
    String notes;
    Boolean isDefaultEquipment;
    String serialNumber;
    String assetTag;


    public static BookingEquipmentResponse fromEntity(BookingEquipment bookingEquipment) {
        return BookingEquipmentResponse.builder()
                .equipmentModelName(bookingEquipment.getItem() != null ? bookingEquipment.getItem().getModel().getName() : null)
                .notes(bookingEquipment.getNotes())
                .isDefaultEquipment(bookingEquipment.getIsDefaultEquipment())
                .serialNumber(bookingEquipment.getItem() != null ? bookingEquipment.getItem().getSerialNumber() : null)
                .assetTag(bookingEquipment.getItem() != null ? bookingEquipment.getItem().getAssetTag() : null)
                .build();
    }
}
