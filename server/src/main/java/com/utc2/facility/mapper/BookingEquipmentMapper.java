package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BookingEquipmentCreationRequest;
import com.utc2.facility.dto.request.BookingEquipmentUpdateRequest;
import com.utc2.facility.dto.response.BookingEquipmentResponse;
import com.utc2.facility.entity.BookingEquipment;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {EquipmentItem.class, EquipmentModel.class}
)
public interface BookingEquipmentMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "equipmentModelName", source = "item.model.name")
    @Mapping(target = "serialNumber", source = "item.serialNumber")
    @Mapping(target = "assetTag", source = "item.assetTag")
    BookingEquipmentResponse toBookingEquipmentResponse(BookingEquipment bookingEquipment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "isDefaultEquipment", source = "isDefaultEquipment")
    @Mapping(target = "notes", source = "notes")
    BookingEquipment toBookingEquipment(BookingEquipmentCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "isDefaultEquipment", ignore = true)
    void updateBookingEquipment(@MappingTarget BookingEquipment bookingEquipment, BookingEquipmentUpdateRequest request); // Sử dụng DTO Update
}