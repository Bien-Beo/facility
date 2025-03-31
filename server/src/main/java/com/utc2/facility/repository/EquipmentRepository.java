package com.utc2.facility.repository;

import com.utc2.facility.entity.Equipment;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    Optional<Equipment> findByName(String name);
    Optional<Equipment> findBySlug(String slug);

    @Query("SELECT e FROM Equipment e WHERE e.room = :room")
    List<Equipment> findByRoom(@Param("room") Room room);

//    @Query("SELECT COUNT(r) FROM Room r WHERE r.facilityManager.userId = :userId")
//    int countRoomsManagedByUser(@Param("userId") String userId);

}
