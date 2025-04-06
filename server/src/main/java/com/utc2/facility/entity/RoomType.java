package com.utc2.facility.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "room_type", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type_name"})
})
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    String id;

    @Column(name = "type_name", unique = true, nullable = false)
    @NotBlank(message = "Tên loại phòng không được trống")
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    List<Room> rooms;
}