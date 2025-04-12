package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BuildingCreationRequest;
import com.utc2.facility.dto.request.BuildingUpdateRequest;
import com.utc2.facility.dto.request.RoomTypeCreationRequest;
import com.utc2.facility.dto.request.RoomTypeUpdateRequest;
import com.utc2.facility.dto.response.BuildingResponse;
import com.utc2.facility.dto.response.RoomTypeResponse;
import com.utc2.facility.entity.Building;
import com.utc2.facility.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomTypeMapper {

    // Map sang Response
    @Mapping(target = "roomList", ignore = true)
    RoomTypeResponse toRoomTypeResponse(RoomType roomType);

    // Map từ Creation DTO
    RoomType toRoomType(RoomTypeCreationRequest request);

    // Map từ Update DTO
    void updateRoomType(@MappingTarget RoomType roomType, RoomTypeUpdateRequest request);
}
