package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BuildingCreationRequest;
import com.utc2.facility.dto.request.BuildingUpdateRequest;
import com.utc2.facility.dto.response.BuildingResponse;
import com.utc2.facility.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BuildingMapper {

    // Map sang Response
    @Mapping(target = "roomList", ignore = true)
    BuildingResponse toBuildingResponse(Building building);

    // Map từ Creation DTO
    Building toBuilding(BuildingCreationRequest request);

    // Map từ Update DTO
    void updateBuilding(@MappingTarget Building building, BuildingUpdateRequest request);
}
