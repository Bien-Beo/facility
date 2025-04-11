package com.utc2.facility.dto.response;

import com.utc2.facility.entity.RoomType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponse {

    String id;
    String name;
    String description;
    List<RoomResponse> roomList;

    public static RoomTypeResponse fromEntity(RoomType roomType) {
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .build();
    }
}