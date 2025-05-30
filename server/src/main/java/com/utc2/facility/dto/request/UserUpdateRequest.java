package com.utc2.facility.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest { // Cập nhật Hồ sơ cá nhân

     String username;

     @Size(min = 1, max = 255, message = "Họ tên phải có độ dài từ 1 đến 255 ký tự")
     String fullName;

     String avatar;

     String roleName;
}