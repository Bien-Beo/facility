package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.CancelRequestCreationRequest;
import com.utc2.facility.dto.request.CancelRequestUpdateRequest;
import com.utc2.facility.dto.request.RepairRequestCreationRequest;
import com.utc2.facility.dto.response.CancelRequestResponse;
import com.utc2.facility.dto.response.RepairRequestResponse;
import com.utc2.facility.entity.CancelRequest;
import com.utc2.facility.entity.RepairRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RepairRequestMapper {
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "equipmentName", source = "equipment.name")
    RepairRequestResponse toRepairRequestResponse(RepairRequest repairRequest);

    RepairRequest toRepairRequest(RepairRequestCreationRequest request);

    void updateRepairRequest(@MappingTarget RepairRequest repairRequest, RepairRequestCreationRequest request);
}
