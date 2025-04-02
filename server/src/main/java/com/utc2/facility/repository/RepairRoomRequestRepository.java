package com.utc2.facility.repository;

import com.utc2.facility.entity.RepairRoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRoomRequestRepository extends JpaRepository<RepairRoomRequest, String> {/// /
    List<RepairRoomRequest> findByUserId(String userId);
    Optional<RepairRoomRequest> findByRoomName(String roomName);
}
