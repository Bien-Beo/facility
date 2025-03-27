package com.utc2.facility.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotNull
    String userId;

    @NotNull
    String username;

    @Email
    @NotNull
    String email;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    Set<String> roles;
}
