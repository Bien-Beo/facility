package com.utc2.facility.entity;

import com.utc2.facility.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "total_quantity", nullable = false)
    int totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    int availableQuantity;

    @Column(name = "is_room_specific", nullable = false)
    boolean isRoomSpecific;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    EquipmentStatus status;

    @ManyToOne
    @JoinColumn(name = "equipment_type_id", nullable = false)
    EquipmentType equipmentType;
}