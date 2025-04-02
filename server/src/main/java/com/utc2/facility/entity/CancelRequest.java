package com.utc2.facility.entity;

import com.utc2.facility.enums.CancelStatus;
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
@Table(name = "cancel_request")
public class CancelRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @OneToOne
    @JoinColumn(name = "borrow_request_id", nullable = false, unique = true)
    BorrowRequest borrowRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    CancelStatus status;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}//
