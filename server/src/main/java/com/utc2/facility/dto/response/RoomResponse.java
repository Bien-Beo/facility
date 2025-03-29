package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

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
    String slug;
    String img;
    String status;
    String buildingName;
    String roomTypeName;
    String nameFacilityManager;
    Boolean isActive;
    Date createdAt;
    Date updatedAt;
    Date deletedAt;

    public static RoomResponse fromEntity(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .slug(room.getSlug())
                .img(room.getImg())
                .status(room.getStatus().name())
                .buildingName(room.getBuilding() != null ? room.getBuilding().getName() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getName() : null)
                .nameFacilityManager(room.getFacilityManager() != null ? room.getFacilityManager().getUsername() : null)
                .isActive(room.isActive())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .deletedAt(room.getDeletedAt())
                .build();
    }
}
