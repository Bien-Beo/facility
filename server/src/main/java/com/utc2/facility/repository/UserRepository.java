package com.utc2.facility.repository;

import com.utc2.facility.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(String userId);
}
