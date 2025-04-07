package com.utc2.facility.repository;

import com.utc2.facility.entity.BookingEquipment;
import com.utc2.facility.entity.BookingEquipmentId; // Import lớp ID tổng hợp
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Import nếu cần các phương thức khác
import java.util.Optional;

@Repository
public interface BookingEquipmentRepository extends JpaRepository<BookingEquipment, BookingEquipmentId> {

    List<BookingEquipment> findByBookingId(String bookingId);
    List<BookingEquipment> findByBookingIdAndIsDefaultEquipment(String bookingId, boolean isDefaultEquipment);
    List<BookingEquipment> findByItem_Id(String itemId);
    Optional<BookingEquipment> findById(BookingEquipmentId id);
}