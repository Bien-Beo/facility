package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.RepairEquipmentRequestCreationRequest;
import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairEquipmentRequestResponse;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.entity.RepairEquipmentRequest;
import com.utc2.facility.entity.RepairRoomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RepairEquipmentRequestMapper {//
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "equipmentName", source = "equipment.name")
    RepairEquipmentRequestResponse toRepairEquipmentRequestResponse(RepairEquipmentRequest request);

    RepairEquipmentRequest toRepairEquipmentRequest(RepairEquipmentRequestCreationRequest request);

    void updateRepairEquipmentRequest(@MappingTarget RepairEquipmentRequest repairEquipmentRequest, RepairEquipmentRequestCreationRequest request);
}
