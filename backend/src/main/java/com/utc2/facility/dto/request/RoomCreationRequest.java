package com.utc2.facility.dto.request;

import com.utc2.facility.entity.RoomType;
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
    @NotNull
    String nameTypeRoom;
}
