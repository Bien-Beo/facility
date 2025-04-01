package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.RepairRoomRequestCreationRequest;
import com.utc2.facility.dto.response.RepairRoomRequestResponse;
import com.utc2.facility.entity.RepairRoomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RepairRoomRequestMapper {
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "roomName", source = "room.name")
    RepairRoomRequestResponse toRepairRoomRequestResponse(RepairRoomRequest repairRoomRequest);

    RepairRoomRequest toRepairRoomRequest(RepairRoomRequestCreationRequest request);

    void updateRepairRoomRequest(@MappingTarget RepairRoomRequest repairRoomRequest, RepairRoomRequestCreationRequest request);
}
