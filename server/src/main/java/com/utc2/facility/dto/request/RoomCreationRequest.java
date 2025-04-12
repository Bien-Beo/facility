package com.utc2.facility.dto.request;

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
public class RoomCreationRequest {

    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 255, message = "Tên phòng không được vượt quá 255 ký tự")
    String name;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    String description;

    @Min(value = 1, message = "Sức chứa phải ít nhất là 1")
    int capacity;

    @NotBlank(message = "Id tòa nhà không được để trống")
    String buildingId;

    @NotBlank(message = "Id loại phòng không được để trống")
    String roomTypeId;

    String facilityManagerId;

    @Size(max = 255, message = "Vị trí không được vượt quá 255 ký tự")
    String location;

    @Size(max = 2048, message = "Đường dẫn ảnh không được vượt quá 2048 ký tự")
    String img;
}