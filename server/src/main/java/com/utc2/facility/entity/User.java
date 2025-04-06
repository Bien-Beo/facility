package com.utc2.facility.entity;

import java.util.Set;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

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
    @Column(length = 36)
    String id;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Mã người dùng không được trống")
    String userId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Tên đăng nhập không được trống")
    String username;

    @Column(name = "full_name", length = 255)
    String fullName;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được trống")
    String email;

    @Column(name = "password", nullable = false)
    @ToString.Exclude
    String password;

    @Column(name = "avatar")
    String avatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_name", nullable = false)
    Role role;

    @OneToMany(mappedBy = "facilityManager", fetch = FetchType.LAZY)
    @ToString.Exclude
    Set<Room> managedRooms;

     @CreationTimestamp
     @Column(name = "created_at", nullable = false, updatable = false)
     private Instant createdAt;

     @UpdateTimestamp
     @Column(name = "updated_at", nullable = false)
     private Instant updatedAt;
}