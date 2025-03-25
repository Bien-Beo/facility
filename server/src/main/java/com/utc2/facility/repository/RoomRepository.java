package com.utc2.facility.repository;

import com.utc2.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    boolean existsByName(String name);
    Optional<Room> findByName(String name);
}
