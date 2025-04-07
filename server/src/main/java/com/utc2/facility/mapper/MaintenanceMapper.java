package com.utc2.facility.mapper;

import com.utc2.facility.dto.request.MaintenanceRequest;
import com.utc2.facility.dto.request.MaintenanceUpdate;
import com.utc2.facility.dto.response.MaintenanceResponse;
import com.utc2.facility.entity.MaintenanceTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "modelName", source = "item.model.name")
    @Mapping(target = "reportByUser", source = "reportedBy.fullName")
    @Mapping(target = "technicianName", source = "technician.fullName")
    MaintenanceResponse toMaintenanceResponse(MaintenanceTicket maintenanceTicket);

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "item", ignore = true)
    MaintenanceTicket toMaintenance(MaintenanceRequest request);

    void updateMaintenanceTicket(@MappingTarget MaintenanceTicket maintenanceTicket, MaintenanceUpdate request);
}
