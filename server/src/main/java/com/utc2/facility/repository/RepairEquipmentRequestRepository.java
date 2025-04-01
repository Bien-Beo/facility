package com.utc2.facility.repository;

import com.utc2.facility.entity.RepairEquipmentRequest;
import com.utc2.facility.entity.RepairRoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairEquipmentRequestRepository extends JpaRepository<RepairEquipmentRequest, String> {
    List<RepairEquipmentRequest> findByUserId(String userId);
    List<RepairEquipmentRequest> findByEquipmentId(String equipmentId);
    Optional<RepairEquipmentRequest> findByEquipmentName(String equipmentName);
}
