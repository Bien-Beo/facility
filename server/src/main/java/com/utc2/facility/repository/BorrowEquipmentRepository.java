package com.utc2.facility.repository;

import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.Equipment;
import com.utc2.facility.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, String> {
    Optional<BorrowRequest> findByUser(@Param("user") String user);
    Optional<BorrowRequest> findByRoom(@Param("room") String room);
    List<BorrowRequest> findByUserId(String userId);
    List<BorrowRequest> findByBorrowDate(LocalDateTime borrowDate);
    List<BorrowRequest> findByReturnDate(LocalDateTime returnDate);
}
