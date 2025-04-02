package com.utc2.facility.repository;

import com.utc2.facility.entity.Notification;
import com.utc2.facility.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {//
    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);
}
