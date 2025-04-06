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
public interface BorrowEquipmentRepository extends JpaRepository<BookingEquipment, String> {//
    Optional<BookingEquipment> findByBorrowRequest(Booking booking);
    Optional<BookingEquipment> findByEquipment(EquipmentItem equipmentItem);
    List<BookingEquipment> findByBorrowRequestId(@Param("borrowRequestId") String borrowRequestId);
    List<BookingEquipment> findByEquipmentId(@Param("equipmentId") String equipmentId);
//    List<BorrowRequest> findByUserId(String userId);
//    List<BorrowRequest> findByBorrowDate(LocalDateTime borrowDate);
//    List<BorrowRequest> findByReturnDate(LocalDateTime returnDate);
}
