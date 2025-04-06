package com.utc2.facility.dto.response;

import com.utc2.facility.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

     String id;

     String userId;

     String username;

     String fullName;

     String email;

     String avatar;

     String roleName;

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .roleName(user.getRole() != null ? user.getRole().getName().toString() : null)
                .build();
    }
}