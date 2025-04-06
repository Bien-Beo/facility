package com.utc2.facility.dto.request;

import com.utc2.facility.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomUpdateRequest {

    @Size(max = 255)
    String name;

    @Size(max = 1000)
    String description;

    @Min(value = 1)
    Integer capacity;

    @Size(max = 255)
    String location;

    String buildingName;

    String roomTypeName;

    String facilityManagerId;

    RoomStatus status;

    @Size(max = 2048)
    String img;
}