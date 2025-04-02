package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "room_type")
public class RoomType {//
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;

    @Column(name = "type_name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
     String name;

    @Column(name = "description")
     String description;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Room> rooms;
}

