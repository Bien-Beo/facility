package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.repository.RoomTypeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {//
    @Mapping(target = "buildingName", source = "building.name")
    @Mapping(target = "nameFacilityManager", source = "facilityManager.username")
    @Mapping(target = "roomTypeName", source = "roomType.name")
    RoomResponse toRoomResponse(Room room);

    Room toRoom(RoomCreationRequest request);

    @Mapping(target = "roomType", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "facilityManager", ignore = true)
    void updateRoom(@MappingTarget Room room, RoomCreationRequest request);
}
