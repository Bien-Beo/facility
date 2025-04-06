package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BorrowEquipmentCreationRequest;
import com.utc2.facility.dto.response.BorrowEquipmentResponse;
import com.utc2.facility.entity.BookingEquipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BorrowEquipmentMapper {//
    @Mapping(target = "borrowRequestId", source = "booking.id")
    @Mapping(target = "equipmentName", source = "equipmentItem.name")
    BorrowEquipmentResponse toBorrowEquipmentResponse(BookingEquipment bookingEquipment);

    BookingEquipment toBorrowEquipment(BorrowEquipmentCreationRequest request);

    @Mapping(target = "borrowRequest", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    void updateBorrowEquipment(@MappingTarget BookingEquipment bookingEquipment, BorrowEquipmentCreationRequest request);
}
