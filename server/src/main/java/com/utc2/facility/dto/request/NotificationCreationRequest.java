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
public class NotificationCreationRequest {
    @NotBlank(message = "User ID cannot be blank")
    String userId;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 255, message = "Message cannot exceed 255 characters")
    String message;

    @NotBlank(message = "Type cannot be blank")
    String type;

    @NotBlank(message = "Status cannot be blank")
    String status;

    @NotBlank(message = "Room ID cannot be blank")
    String roomId;

    @NotBlank(message = "Booking ID cannot be blank")
    String bookingId;
}