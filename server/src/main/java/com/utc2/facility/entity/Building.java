package com.utc2.facility.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "building", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    @NotBlank(message = "Tên tòa nhà không được trống")
    private String name;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Room> rooms;

    // @CreationTimestamp
    // @Column(name = "created_at", nullable = false, updatable = false)
    // private Instant createdAt;
    //
    // @UpdateTimestamp
    // @Column(name = "updated_at", nullable = false)
    // private Instant updatedAt;
}