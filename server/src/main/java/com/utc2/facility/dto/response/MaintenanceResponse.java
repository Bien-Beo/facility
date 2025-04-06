package com.utc2.facility.dto.response;

import com.utc2.facility.entity.MaintenanceTicket;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponse {
    String id;
    String roomName;
    String modelName;
    String reportByUser;
    String technicianName;
    String description;
    String notes;
    BigDecimal cost;
    String actionTaken;
    String status;
    LocalDateTime updatedAt;
    LocalDateTime startDate;
    LocalDateTime completionDate;
    LocalDateTime reportDate;


    public static MaintenanceResponse fromEntity(MaintenanceTicket maintenanceTicket) {
        return MaintenanceResponse.builder()
                .id(maintenanceTicket.getId())
                .roomName(maintenanceTicket.getRoom() != null ? maintenanceTicket.getRoom().getName() : null)
                .description(maintenanceTicket.getDescription())
                .status(maintenanceTicket.getStatus().name())
                .reportByUser(maintenanceTicket.getReportedBy() != null ? maintenanceTicket.getReportedBy().getUsername() : null)
                .modelName(maintenanceTicket.getItem().getModel() != null ? maintenanceTicket.getItem().getModel().getName() : null)
                .technicianName(maintenanceTicket.getTechnician() != null ? maintenanceTicket.getTechnician().getUsername() : null)
                .notes(maintenanceTicket.getNotes())
                .cost(maintenanceTicket.getCost())
                .actionTaken(maintenanceTicket.getActionTaken())
                .updatedAt(maintenanceTicket.getUpdatedAt())
                .startDate(maintenanceTicket.getStartDate())
                .completionDate(maintenanceTicket.getCompletionDate())
                .reportDate(maintenanceTicket.getReportDate())
                .build();
    }
}
