package com.utc2.facility.repository;

import com.utc2.facility.entity.BookingEquipment;
import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.EquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingEquipmentRepository extends JpaRepository<BookingEquipment, String> {//
    Optional<BookingEquipment> findByBooking(Booking booking);
    Optional<BookingEquipment> findByItem(EquipmentItem item);
    List<BookingEquipment> findByBookingId(@Param("bookingId") String bookingId);
    List<BookingEquipment> findByItemId(@Param("equipmentId") String equipmentId);
//    List<BorrowRequest> findByUserId(String userId);
//    List<BorrowRequest> findByPlannedStartTime(LocalDateTime borrowDate);
//    List<BorrowRequest> findByReturnDate(LocalDateTime returnDate);
}
