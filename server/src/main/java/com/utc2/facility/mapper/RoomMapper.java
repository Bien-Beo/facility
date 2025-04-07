package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.request.RoomUpdateRequest;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.repository.RoomTypeRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    // Map sang Response
    @Mapping(target = "buildingName", source = "building.name")
    @Mapping(target = "roomTypeName", source = "roomType.name")
    @Mapping(target = "nameFacilityManager", source = "facilityManager.fullName")
    @Mapping(target = "defaultEquipments", ignore = true)
    RoomResponse toRoomResponse(Room room);

    // Map từ Creation DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    @Mapping(target = "facilityManager", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Room toRoom(RoomCreationRequest request);

    // Map từ Update DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    @Mapping(target = "facilityManager", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateRoom(@MappingTarget Room room, RoomUpdateRequest request);
}
