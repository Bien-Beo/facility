package com.utc2.facility.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp; // For automatic createdAt
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp; // For automatic updatedAt

import java.time.Instant;
import java.util.List; // Import List

@Getter
@Setter
@Entity
@Table(name = "equipment_models", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class EquipmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private EquipmentType equipmentType;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    @Column(name = "manufacturer")
    private String manufacturer;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<EquipmentItem> items;
}