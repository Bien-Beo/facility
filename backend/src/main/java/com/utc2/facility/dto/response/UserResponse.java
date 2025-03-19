package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Role;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
     String id;
     String userId;
     String username;
     String email;
     Set<Role> roles;
}
