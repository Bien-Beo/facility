package com.utc2.facility.dto.response;

import com.utc2.facility.entity.BookingEquipment;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BorrowEquipmentResponse {
    String id;
    String borrowRequestId;
    String equipmentName;

    public static BorrowEquipmentResponse fromEntity(BookingEquipment bookingEquipment) {
        return BorrowEquipmentResponse.builder()
                .id(bookingEquipment.getId())
                .borrowRequestId(bookingEquipment.getBooking().getId() == null ? null : bookingEquipment.getBooking().getId())
                .equipmentName(bookingEquipment.getEquipmentItem().getName() == null ? null : bookingEquipment.getEquipmentItem().getName())
                .build();
    }
}//
