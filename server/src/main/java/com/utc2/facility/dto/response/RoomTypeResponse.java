package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Building;
import com.utc2.facility.entity.Room;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BuildingResponse {

    String id;
    String name;
    List<RoomResponse> roomList;

    public static BuildingResponse fromEntity(Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .build();
    }
}