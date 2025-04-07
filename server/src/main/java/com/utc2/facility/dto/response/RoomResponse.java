package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Room;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    String id;
    String name;
    String description;
    int capacity;
    String img;
    String status;
    String buildingName;
    String roomTypeName;
    String nameFacilityManager;
    String location;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;

    List<EquipmentResponse> defaultEquipments;

    public static RoomResponse fromEntity(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .img(room.getImg())
                .status(room.getStatus() != null ? room.getStatus().name() : null)
                .buildingName(room.getBuilding() != null ? room.getBuilding().getName() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getName() : null)
                .nameFacilityManager(room.getFacilityManager() != null ? room.getFacilityManager().getUsername() : null)
                .location(room.getLocation())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .deletedAt(room.getDeletedAt())
                .build();
    }
}