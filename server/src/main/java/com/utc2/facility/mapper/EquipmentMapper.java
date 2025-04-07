package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.EquipmentItemCreationRequest;
import com.utc2.facility.dto.request.EquipmentItemUpdateRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.EquipmentItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    // Map sang Response DTO
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "typeName", source = "model.name")
    @Mapping(target = "defaultRoomName", source = "defaultRoom.name")
    EquipmentResponse toEquipmentResponse(EquipmentItem item);

    // Map từ Creation DTO sang Entity (chỉ map trường đơn giản)
    @Mapping(target="id", ignore=true)
    @Mapping(target="model", ignore=true)
    @Mapping(target="defaultRoom", ignore=true)
    @Mapping(target="status", ignore=true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="updatedAt", ignore=true)
    EquipmentItem toEquipmentItem(EquipmentItemCreationRequest request);

    // Update Entity từ Update DTO
    @Mapping(target="id", ignore=true)
    @Mapping(target="model", ignore=true)
    @Mapping(target="serialNumber", ignore=true)
    @Mapping(target="purchaseDate", ignore=true)
    @Mapping(target="warrantyExpiryDate", ignore=true)
    @Mapping(target="defaultRoom", ignore=true)
    @Mapping(target="status", ignore = true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="updatedAt", ignore=true)
    void updateEquipmentItem(@MappingTarget EquipmentItem item, EquipmentItemUpdateRequest request);
}
