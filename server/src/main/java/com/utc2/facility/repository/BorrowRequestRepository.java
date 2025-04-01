package com.utc2.facility.repository;

import com.utc2.facility.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, String> {
    List<BorrowRequest> findByUserId(String userId);
    List<BorrowRequest> findByBorrowDate(LocalDateTime borrowDate);
    List<BorrowRequest> findByReturnDate(LocalDateTime returnDate);
    List<BorrowRequest> findByExpectedReturnDate(LocalDateTime returnExpectedDate);
}
