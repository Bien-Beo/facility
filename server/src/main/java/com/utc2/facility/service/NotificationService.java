package com.utc2.facility.service;

import com.utc2.facility.entity.BorrowRequest;
import com.utc2.facility.entity.Notification;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.NotificationStatus;
import com.utc2.facility.enums.NotificationType;
import com.utc2.facility.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//    private final BorrowRequestRepository borrowRequestRepository;
//
//    // Gửi thông báo trước 1 giờ khi mượn phòng
//    @Scheduled(fixedRate = 60000) // Chạy mỗi phút (1 phút kiểm tra 1 lần)
//    public void sendUpcomingBorrowNotifications() {
//        LocalDateTime oneHourLater = LocalDateTime.now().plusHours(1);
//
//        List<BorrowRequest> upcomingBorrows = borrowRequestRepository.findByBorrowTime(oneHourLater);
//
//        for (BorrowRequest borrow : upcomingBorrows) {
//            Notification notification = Notification.builder()
//                    .user(borrow.getUser())
//                    .message("Bạn sắp mượn phòng " + borrow.getRoom().getName() + " lúc " + borrow.getBorrowTime())
//                    .type(NotificationType.BORROW)
//                    .status(NotificationStatus.UNREAD)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//            notificationRepository.save(notification);
//        }
//    }
}
