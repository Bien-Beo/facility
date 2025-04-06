package com.utc2.facility.repository;

import com.utc2.facility.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<Booking, String> {//
    List<Booking> findByUserId(String userId);
    List<Booking> findByBorrowDate(LocalDateTime borrowDate);
    List<Booking> findByReturnDate(LocalDateTime returnDate);
    List<Booking> findByExpectedReturnDate(LocalDateTime returnExpectedDate);
}