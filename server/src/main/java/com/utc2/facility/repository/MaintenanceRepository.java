package com.utc2.facility.repository;

import com.utc2.facility.entity.MaintenanceTicket;
import com.utc2.facility.enums.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceTicket, String> {
    List<MaintenanceTicket> findByReportedBy_Id(String reportedId);
    List<MaintenanceTicket> findByRoomName(String roomName);
    Page<MaintenanceTicket> findByTechnician_Id(String technicianId, Pageable pageable);
    Page<MaintenanceTicket> findByTechnician_IdAndStatusIn(String technicianId, List<MaintenanceStatus> statuses, Pageable pageable);
}
