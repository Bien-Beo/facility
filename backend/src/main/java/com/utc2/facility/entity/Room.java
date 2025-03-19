package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;

    @Column(name = "name", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
     String name;

    @Column(name = "description")
    String description;

    @Column(name = "capacity")
     int capacity;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    @JsonBackReference
    RoomType roomType;
}

