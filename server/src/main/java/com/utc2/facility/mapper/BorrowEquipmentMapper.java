package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BorrowEquipmentCreationRequest;
import com.utc2.facility.dto.request.BorrowRequestCreationRequest;
import com.utc2.facility.dto.response.BorrowEquipmentResponse;
import com.utc2.facility.dto.response.BorrowRequestResponse;
import com.utc2.facility.entity.BorrowEquipment;
import com.utc2.facility.entity.BorrowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BorrowEquipmentMapper {//
    @Mapping(target = "borrowRequestId", source = "borrowRequest.id")
    @Mapping(target = "equipmentName", source = "equipment.name")
    BorrowEquipmentResponse toBorrowEquipmentResponse(BorrowEquipment borrowEquipment);

    BorrowEquipment toBorrowEquipment(BorrowEquipmentCreationRequest request);

    @Mapping(target = "borrowRequest", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    void updateBorrowEquipment(@MappingTarget BorrowEquipment borrowEquipment, BorrowEquipmentCreationRequest request);
}
