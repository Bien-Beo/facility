package com.utc2.facility.entity;

import com.utc2.facility.enums.NotificationStatus;
import com.utc2.facility.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "notification")
public class Notification {//

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "message", nullable = false)
    String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    NotificationStatus status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime createdAt;
}
