package com.utc2.facility.dto.request;

import com.utc2.facility.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingUpdateRequest {

    @NotBlank(message = "Tên tòa nhà không được trống")
    @Size(max = 255, message = "Tên tòa nhà không được vượt quá 255 ký tự")
    String name;

}