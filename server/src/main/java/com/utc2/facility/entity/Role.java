package com.utc2.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "role")
public class Role {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 36, unique = true)
    com.utc2.facility.enums.Role name; // Dùng enum làm khóa chính

    @Column(name = "description")
    String description;

    @ManyToMany
    Set<Permission> permissions;
}

