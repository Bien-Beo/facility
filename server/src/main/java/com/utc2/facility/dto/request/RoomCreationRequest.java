package com.utc2.facility.dto.request;

import com.utc2.facility.entity.Building;
import com.utc2.facility.entity.RoomType;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationRequest {
    @NotNull
    String name;

    String description;

    @NotNull
    int capacity;

    String buildingName;

    RoomStatus status;

    String img;

    boolean isActive = true;

    String facilityManagerId;

    String slug;

    String nameTypeRoom;
}
