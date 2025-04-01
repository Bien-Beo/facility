package com.utc2.facility.repository;

import com.utc2.facility.entity.CancelRequest;
import com.utc2.facility.entity.RepairRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, String> {
    List<RepairRequest> findByUserId(String userId);
    Optional<RepairRequest> findByRoomName(String roomName);
    Optional<RepairRequest> findByEquipmentId(String equipmentId);
    List<RepairRequest> findByIsRoomIssue(Boolean isRoomIssue);
}
