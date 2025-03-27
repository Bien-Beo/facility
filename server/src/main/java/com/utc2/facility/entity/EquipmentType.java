package com.utc2.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "equipment_type")
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "type_name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "equipmentType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Equipment> equipmentList;
}
