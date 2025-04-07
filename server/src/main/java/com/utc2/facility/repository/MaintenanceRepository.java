package com.utc2.facility.repository;

import com.utc2.facility.entity.MaintenanceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceTicket, String> {
    List<MaintenanceTicket> findByReportedBy_Id(String reportedId);
    List<MaintenanceTicket> findByRoomName(String roomName);
}
