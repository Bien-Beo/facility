package com.utc2.facility.dto.request;

import com.utc2.facility.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateRequest {

    @Size(max = 255, message = "Tên phòng không được vượt quá 255 ký tự")
    String name;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    String description;

    @Min(value = 1, message = "Sức chứa phải ít nhất là 1")
    Integer capacity;

    @Size(max = 255, message = "Vị trí không được vượt quá 255 ký tự")
    String location;

    String buildingName;

    String roomTypeName;

    String facilityManagerId;

    RoomStatus status;

    @Size(max = 2048, message = "Đường dẫn ảnh không được vượt quá 2048 ký tự")
    String img;
}