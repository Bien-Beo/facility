package com.utc2.facility.repository;

import com.utc2.facility.entity.Booking;
import com.utc2.facility.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Page<Booking> findByUser_Id(String userId, Pageable pageable);
    List<Booking> findByUser_Id(String userId);
    Page<Booking> findByRoom_Id(String roomId, Pageable pageable);
    List<Booking> findByPlannedStartTime(LocalDateTime plannedStartTime);
    List<Booking> findByPlannedEndTime(LocalDateTime plannedEndTime);
    List<Booking> findByActualCheckInTime(LocalDateTime actualCheckInTime);
    List<Booking> findByActualCheckOutTime(LocalDateTime actualCheckOutTime);
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Booking b WHERE b.room.id = :roomId AND b.status IN :statuses " +
            "AND b.plannedStartTime < :endTime AND b.plannedEndTime > :startTime")
    boolean existsOverlappingBookingForRoom(@Param("roomId") String roomId,
                                            @Param("statuses") List<BookingStatus> statuses,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);
    @Query("SELECT DISTINCT be.item.id FROM BookingEquipment be JOIN be.booking b " +
            "WHERE be.item.id IN :itemIds AND b.status IN :statuses " +
            "AND b.plannedStartTime < :endTime AND b.plannedEndTime > :startTime")
    List<String> findUnavailableItemsInTimeRange(@Param("itemIds") Set<String> itemIds,
                                                 @Param("statuses") List<BookingStatus> statuses,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    Page<Booking> findAll(Specification<Booking> and, Pageable pageable);
    List<Booking> findByStatus(BookingStatus status);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    List<Booking> findByPlannedStartTimeAndBorrowNotifiedFalse(LocalDateTime time);
    List<Booking> findByPlannedEndTimeAndReturnNotifiedFalse(LocalDateTime time);
    public List<Booking> findByPlannedEndTimeBeforeAndReturnNotifiedFalse(LocalDateTime now);
    public List<Booking> findByStatusAndApprovedNotifiedFalse(BookingStatus status);
}