package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.utc2.facility.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
     String id;

    @Column(name = "name", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
     String name;

    @Column(name = "description")
    String description;

    @Column(name = "capacity")
     int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    RoomStatus status;

    @ManyToOne
    @JoinColumn(name = "fm_id", nullable = false)
    @JsonBackReference
    User fm;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    @JsonBackReference
    RoomType roomType;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<RoomEquipment> roomEquipments;
}

