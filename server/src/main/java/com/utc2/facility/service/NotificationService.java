package com.utc2.facility.service;

import com.utc2.facility.dto.request.NotificationCreationRequest;
import com.utc2.facility.dto.response.NotificationResponse;
import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.Notification;
import com.utc2.facility.entity.Room;
import com.utc2.facility.entity.User;
import com.utc2.facility.enums.BookingStatus;
import com.utc2.facility.enums.NotificationStatus;
import com.utc2.facility.enums.NotificationType;
import com.utc2.facility.mapper.NotificationMapper;
import com.utc2.facility.repository.BookingRepository;
import com.utc2.facility.repository.NotificationRepository;
import com.utc2.facility.repository.RoomRepository;
import com.utc2.facility.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final NotificationMapper notificationMapper;

    // Gửi thông báo trước 1 giờ khi mượn phòng
    @Scheduled(fixedRate = 60000)
    public void sendUpcomingBorrowNotifications() {
        LocalDateTime oneHourLater = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);

        List<Booking> upcomingBookings = bookingRepository
                .findByPlannedStartTimeAndBorrowNotifiedFalse(oneHourLater);

        List<Booking> bookingsToUpdate = new ArrayList<>();

        for (Booking booking : upcomingBookings) {
            Notification notification = Notification.builder()
                    .user(booking.getUser())
                    .message("Bạn sắp mượn phòng " + booking.getRoom().getName() + " lúc " + booking.getPlannedStartTime())
                    .type(NotificationType.BORROW)
                    .status(NotificationStatus.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            booking.setBorrowNotified(true);
            bookingsToUpdate.add(booking);  // Chỉ thêm vào danh sách chờ cập nhật
        }

        // Sau khi vòng lặp hoàn thành, cập nhật tất cả các booking cùng một lúc
        bookingRepository.saveAll(bookingsToUpdate);

    }

    // Gửi thông báo trước 1 giờ khi trả phòng
    @Scheduled(fixedRate = 60000)
    public void sendUpcomingReturnNotifications() {
        LocalDateTime oneHourLater = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);

        List<Booking> upcomingBookings = bookingRepository
                .findByPlannedEndTimeAndReturnNotifiedFalse(oneHourLater);

        for (Booking booking : upcomingBookings) {
            Notification notification = Notification.builder()
                    .user(booking.getUser())
                    .message("Bạn sắp trả phòng " + booking.getRoom().getName() + " lúc " + booking.getPlannedEndTime())
                    .type(NotificationType.RETURN)
                    .status(NotificationStatus.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            // Đánh dấu đã gửi
            booking.setReturnNotified(true);
            bookingRepository.save(booking);
        }
    }

    // Gửi thông báo khi có booking được phê duyệt
    @Scheduled(fixedRate = 60000)
    public void sendApprovedBookingNotifications() {
        // Tìm tất cả các booking đã được phê duyệt và chưa gửi thông báo
        List<Booking> approvedBookings = bookingRepository
                .findByStatusAndApprovedNotifiedFalse(BookingStatus.CONFIRMED);

        List<Booking> bookingsToUpdate = new ArrayList<>();
        for (Booking booking : approvedBookings) {
            Notification notification = Notification.builder()
                    .user(booking.getUser())
                    .message("Booking của bạn đã được phê duyệt cho phòng " + booking.getRoom().getName())
                    .type(NotificationType.BORROW)
                    .status(NotificationStatus.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            // Đánh dấu booking là đã thông báo
            booking.setApprovedNotified(true);
            bookingsToUpdate.add(booking);
        }

        // Cập nhật tất cả booking đã được thông báo
        bookingRepository.saveAll(bookingsToUpdate);
    }


    @Scheduled(fixedRate = 60000)
    public void sendOverdueBookingNotifications() {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tìm các booking đã quá hạn mà chưa được trả phòng và chưa gửi thông báo
        List<Booking> overdueBookings = bookingRepository
                .findByPlannedEndTimeBeforeAndReturnNotifiedFalse(now);

        // Duyệt qua các booking quá hạn để gửi thông báo
        List<Booking> bookingsToUpdate = new ArrayList<>();

        for (Booking booking : overdueBookings) {
            Notification notification = Notification.builder()
                    .user(booking.getUser())
                    .message("Booking của bạn đã quá hạn trả phòng. Vui lòng trả phòng " + booking.getRoom().getName())
                    .type(NotificationType.RETURN)
                    .status(NotificationStatus.UNREAD)
                    .createdAt(now)
                    .build();

            notificationRepository.save(notification);

            booking.setReturnNotified(true);
            bookingsToUpdate.add(booking);
        }

        bookingRepository.saveAll(bookingsToUpdate);  // Cập nhật tất cả các booking sau khi vòng lặp hoàn thành

    }

    public void delete(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
    }

    public Page<NotificationResponse> getAll(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Page<Notification> notifications = notificationRepository.findByUser(user, pageable);
        return notifications.map(notificationMapper::toResponse);
    }

    public NotificationResponse create(NotificationCreationRequest request) {
        log.info("Creating notification: {}", request);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Notification notification = Notification.builder()
                .user(user)
                .room(room)
                .booking(booking)
                .message(request.getMessage())
                .type(NotificationType.valueOf(request.getType()))
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationMapper.toResponse(notification);
    }

    public NotificationResponse markNotificationAsRead(String notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.setStatus(NotificationStatus.READ);

            Notification saved = notificationRepository.save(notification);

            log.info("Marking notification as read: {}", notificationId);
            return notificationMapper.toResponse(saved);
        } catch (Exception e) {
            log.error("Lỗi khi mark as read notification {}: {}", notificationId, e.getMessage(), e);
            throw e;
        }
    }

}
