package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.EquipmentItemCreationRequest;
import com.utc2.facility.dto.request.EquipmentItemUpdateRequest;
import com.utc2.facility.dto.response.EquipmentModelResponse;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentModelMapper {

    // Map sang Response DTO
    @Mapping(target = "equipmentTypeName", source = "equipmentType.name")
    EquipmentModelResponse toEquipmentModelResponse(EquipmentModel model);
}
