package com.utc2.facility.entity;

import com.utc2.facility.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
//
//    @Getter
//    @Setter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @FieldDefaults(level = AccessLevel.PRIVATE)
//    @Entity
//    @Table(name = "equipment")
//    public class Equipment {
//        @Id
//        @GeneratedValue(strategy = GenerationType.UUID)
//        @Column(name = "id", length = 36)
//        String id;
//
//        @Column(name = "name", nullable = false)
//        String name;
//
//        @Column(name = "description")
//        String description;
//
//        @ManyToOne
//        @JoinColumn(name = "room_id")
//        private Room room;
//
//        @Enumerated(EnumType.STRING)
//        @Column(name = "status", nullable = false, length = 20)
//        EquipmentStatus status;
//
//        @Column(name = "img")
//        private String img;
//
//        @Column(name = "slug", unique = true)
//        private String slug;
//
//        @Column(name = "is_active")
//        private Boolean isActive = true;
//
//        @Column(nullable = false, updatable = false)
//        @Temporal(TemporalType.TIMESTAMP)
//        private Date createdAt = new Date();
//
//        @Column(name = "update_at")
//        @Temporal(TemporalType.TIMESTAMP)
//        private Date updatedAt;
//
//        @Column(name = "deleted_at")
//        @Temporal(TemporalType.TIMESTAMP)
//        private Date deletedAt;
//
//        @ManyToOne
//        @JoinColumn(name = "equipment_type_id", nullable = false)
//        EquipmentType equipmentType;
//
//        @ManyToOne
//        @JoinColumn(name = "equipment_manager_id")
//        private User equipmentManager;
//    }
}
