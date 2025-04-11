package com.utc2.facility.repository;

import com.utc2.facility.entity.User;
import com.utc2.facility.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(@NotBlank(message = "Mã người dùng không được trống") String userId);

    boolean existsByUsername(@NotNull String username);

    boolean existsByEmail(@Email @NotNull String email);

    void deleteByUserId(String userId);

    List<User> findByRole_Name(Role role);
}
