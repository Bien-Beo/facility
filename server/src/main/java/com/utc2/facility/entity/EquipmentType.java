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
@Table(name = "equipment_type",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"name"}) })
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_type_id")
    private EquipmentType parentType;

    @OneToMany(mappedBy = "parentType", fetch = FetchType.LAZY)
    private List<EquipmentType> childTypes;

    @OneToMany(mappedBy = "equipmentType", fetch = FetchType.LAZY)
    private List<EquipmentModel> equipmentModels;
}