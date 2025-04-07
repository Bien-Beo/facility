package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utc2.facility.enums.RoomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room")
@Where(clause = "deleted_at IS NULL")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    String id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 255)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Min(0)
    @Column(name = "capacity")
    int capacity;

    @Column(name = "location", length = 255)
    String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "img", length = 2048)
    String img;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    @JsonIgnore
    Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    @JsonIgnore
    RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fm_id")
    @JsonIgnore
    User facilityManager;

    // --- Metadata & Timestamps ---

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = LocalDateTime.now();
    }
}