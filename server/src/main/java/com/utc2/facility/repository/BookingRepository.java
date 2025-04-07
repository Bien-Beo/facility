package com.utc2.facility.repository;

import com.utc2.facility.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Page<Booking> findByUser_Id(String userId, Pageable pageable);
    List<Booking> findByUser_Id(String userId);
    List<Booking> findByPlannedStartTime(LocalDateTime plannedStartTime);
    List<Booking> findByPlannedEndTime(LocalDateTime plannedEndTime);
    List<Booking> findByActualCheckInTime(LocalDateTime actualCheckInTime);
    List<Booking> findByActualCheckOutTime(LocalDateTime actualCheckOutTime);
}