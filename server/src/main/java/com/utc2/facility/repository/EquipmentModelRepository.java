package com.utc2.facility.repository;

import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, String> {//

    Optional<EquipmentItem> findEquipmentItemByE(String id);
    Optional<EquipmentItem> findBySlug(String slug);

    @Query("SELECT e FROM EquipmentItem e WHERE e.room = :room")
    List<EquipmentItem> findByRoom(@Param("room") Room room);

//    @Query("SELECT COUNT(r) FROM Room r WHERE r.facilityManager.userId = :userId")
//    int countRoomsManagedByUser(@Param("userId") String userId);

}
