package com.utc2.facility.enums;

public enum BookingStatus {
    PENDING_APPROVAL, // Chờ duyệt
    CONFIRMED,        // Đã duyệt (sẵn sàng cho mượn/check-out)
    REJECTED,         // Bị từ chối
    CANCELLED,        // Đã hủy (bởi người dùng hoặc admin)
    IN_PROGRESS,      // Đang được mượn (sau khi check-out, trước check-in) - Tùy chọn nếu có quy trình check-out/in
    COMPLETED,        // Đã hoàn thành (đã trả/check-in)
    OVERDUE           // Quá hạn trả (cần logic riêng để cập nhật)
}