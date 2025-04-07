package com.utc2.facility.repository;

import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.User;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {//
    boolean existsByName(String name);
    Optional<Room> findByName(String name);
    Optional<Room> findById(String id);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.facilityManager.userId = :userId")
    int countRoomsManagedByUser(@Param("userId") String userId);

    List<Room> findByFacilityManager(User facilityManager);

    boolean existsByNameAndIdNot(@Size(max = 255) String name, String id);

}
