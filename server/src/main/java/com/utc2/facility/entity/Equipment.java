package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utc2.facility.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore
     Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    EquipmentStatus status;

    @Column(name = "img")
     String img;

    @Column(name = "slug", unique = true)
     String slug;

    @Column(name = "is_active")
     Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
     Date createdAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
     Date updatedAt;

    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
     Date deletedAt;

    @ManyToOne
    @JoinColumn(name = "equipment_type_id", nullable = false)
    @JsonIgnore
    EquipmentType equipmentType;

    @ManyToOne
    @JoinColumn(name = "equipment_manager_id")
    @JsonIgnore
     User equipmentManager;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}//