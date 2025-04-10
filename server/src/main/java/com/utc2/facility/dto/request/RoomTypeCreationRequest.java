package com.utc2.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeCreationRequest {

    @NotBlank(message = "Tên tòa nhà không được trống")
    @Size(max = 255, message = "Tên tòa nhà không được vượt quá 255 ký tự")
    String name;

    String description;
}