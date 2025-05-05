package com.utc2.facility.repository;

import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.Notification;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.NotificationStatus;
import com.utc2.facility.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);
    Page<Notification> findByUser(User user, Pageable pageable);
    boolean existsByBookingAndType(Booking booking, NotificationType type);
    Optional<Notification> findById(String id);
    boolean existsByBookingAndTypeAndCreatedAtAfter(
            Booking booking,
            NotificationType type,
            LocalDateTime createdAt
    );
}
