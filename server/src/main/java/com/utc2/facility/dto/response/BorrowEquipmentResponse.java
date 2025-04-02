package com.utc2.facility.dto.response;

import com.utc2.facility.entity.BorrowEquipment;
import com.utc2.facility.entity.BorrowRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BorrowEquipmentResponse {
    String id;
    String borrowRequestId;
    String equipmentName;

    public static BorrowEquipmentResponse fromEntity(BorrowEquipment borrowEquipment) {
        return BorrowEquipmentResponse.builder()
                .id(borrowEquipment.getId())
                .borrowRequestId(borrowEquipment.getBorrowRequest().getId() == null ? null : borrowEquipment.getBorrowRequest().getId())
                .equipmentName(borrowEquipment.getEquipment().getName() == null ? null : borrowEquipment.getEquipment().getName())
                .build();
    }
}//
