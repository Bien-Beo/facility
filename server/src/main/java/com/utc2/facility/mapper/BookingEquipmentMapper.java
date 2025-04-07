package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.BookingEquipmentCreationRequest;
import com.utc2.facility.dto.request.BookingEquipmentUpdateRequest; // RECOMMENDED: Create and use this DTO
import com.utc2.facility.dto.response.BookingEquipmentResponse;
import com.utc2.facility.entity.BookingEquipment;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Good for updates
public interface BookingEquipmentMapper {

    // --- Mapping to Response ---
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "equipmentModelName", source = "item.model.name")
    BookingEquipmentResponse toBookingEquipmentResponse(BookingEquipment bookingEquipment);


    // --- Mapping from Creation Request ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "item", ignore = true)
    BookingEquipment toBookingEquipment(BookingEquipmentCreationRequest request);


    // --- Mapping for Update ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "item", ignore = true)
    void updateBookingEquipment(@MappingTarget BookingEquipment bookingEquipment, BookingEquipmentUpdateRequest request);
}