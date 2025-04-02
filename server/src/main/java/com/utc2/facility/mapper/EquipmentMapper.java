package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.EquipmentCreationRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.Equipment;
import com.utc2.facility.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {//
    @Mapping(target = "equipmentTypeName", source = "equipmentType.name")
    @Mapping(target = "nameFacilityManager", source = "equipmentManager.username")
    @Mapping(target = "roomName", source = "room.name")
    EquipmentResponse toEquipmentResponse(Equipment equipment);

    Equipment toEquipment(EquipmentCreationRequest request);

    @Mapping(target = "equipmentType", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "equipmentManager", ignore = true)
    void updateEquipment(@MappingTarget Equipment equipment, EquipmentCreationRequest request);
}
