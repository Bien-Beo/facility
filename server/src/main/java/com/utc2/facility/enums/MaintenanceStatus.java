package com.utc2.facility.enums;
public enum MaintenanceStatus {
    REPORTED,      // Mới báo cáo
    ASSIGNED,      // Đã gán cho KTV
    IN_PROGRESS,   // Đang xử lý
    COMPLETED,     // Đã hoàn thành
    CANNOT_REPAIR, // Không thể sửa
    CANCELLED      // Đã hủy (ví dụ báo cáo nhầm)
}