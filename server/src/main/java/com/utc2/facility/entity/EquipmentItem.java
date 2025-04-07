package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utc2.facility.enums.EquipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "equipment_item", indexes = {
        @Index(name = "idx_item_model_id", columnList = "model_id"),
        @Index(name = "idx_item_default_room_id", columnList = "default_room_id"),
        @Index(name = "idx_item_status", columnList = "status"),
        @Index(name = "idx_item_serial_number", columnList = "serial_number", unique = true),
        @Index(name = "idx_item_asset_tag", columnList = "asset_tag", unique = true)
})

@Where(clause = "deleted_at IS NULL")
public class EquipmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    @JsonIgnore
    EquipmentModel model;

    @Column(name = "serial_number", unique = true)
    @Size(max = 255)
    String serialNumber;

    @Column(name = "asset_tag", unique = true)
    @Size(max = 255)
    String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_room_id")
    @JsonIgnore
    Room defaultRoom;

    @Column(name = "purchase_date")
    LocalDate purchaseDate;

    @Column(name = "warranty_expiry_date")
    LocalDate warrantyExpiryDate;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = EquipmentStatus.DISPOSED;
    }
}