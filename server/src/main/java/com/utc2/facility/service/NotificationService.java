package com.utc2.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {//
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
