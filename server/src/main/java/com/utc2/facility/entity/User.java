package com.utc2.facility.entity;

import java.util.Set;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
     String id;

    @Column(name = "user_id", unique = true)
     String userId;

    @Column(name = "username", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
     String username;

    @Column(name = "email", unique = true, nullable = false)
    @NotNull
     String email;

    @Column(name = "password")
     String password;

    @Column(name = "avatar")
    String avatar;

    @ManyToMany
    Set<Role> roles;

    @OneToMany(mappedBy = "facilityManager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Room> managedRooms;

    @ManyToMany
    Set<Equipment> managedEquipments;
}

