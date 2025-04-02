package com.utc2.facility.repository;

import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.CancelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancelRequestRepository extends JpaRepository<CancelRequest, String> {//
    List<CancelRequest> findByUserId(String userId);
    Optional<CancelRequest> findByBorrowRequestId(String borrowRequestId);
}
