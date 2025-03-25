package com.utc2.facility.dto.response;

import com.utc2.facility.entity.RoomType;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String roomTypeName;
}
