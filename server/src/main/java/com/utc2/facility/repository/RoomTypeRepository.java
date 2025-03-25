package com.utc2.facility.repository;

import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, String> {
    boolean existsByName(String name);
    Optional<RoomType> findByName(String name);
}
