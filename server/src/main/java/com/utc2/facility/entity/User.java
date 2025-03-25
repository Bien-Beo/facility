package com.utc2.facility.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;

    @Column(name = "user_id", unique = true)
     String userId;

    @Column(name = "username", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
     String username;

    @Column(name = "email")
     String email;

    @Column(name = "password")
     String password;

    @ManyToMany
    Set<Role> roles;
}

