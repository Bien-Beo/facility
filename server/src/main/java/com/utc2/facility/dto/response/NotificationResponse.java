package com.utc2.facility.dto.response;

import com.utc2.facility.entity.Notification;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    String id;
    String name;
    String message;
    String type;
    String status;
    String createdAt;
    String userId;
    String roomId;
    String bookingId;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .name(notification.getUser().getFullName())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt().toString())
                .userId(notification.getUser().getId())
                .roomId(notification.getRoom().getId())
                .bookingId(notification.getBooking().getId())
                .build();
    }
}