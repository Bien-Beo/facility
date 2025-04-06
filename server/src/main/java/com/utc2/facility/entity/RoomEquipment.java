package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "room_equipment")
public class RoomEquipment {//
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    Room room;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    EquipmentItem equipmentItem;

    @Column(name = "quantity", nullable = false)
    int quantity;
}
