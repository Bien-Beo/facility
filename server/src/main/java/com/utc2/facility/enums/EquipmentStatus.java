package com.utc2.facility.enums;

public enum EquipmentStatus {
    AVAILABLE,         // Sẵn sàng để sử dụng (không hỏng, không bảo trì, không bị hủy bỏ)
    BROKEN,            // Bị hỏng
    UNDER_MAINTENANCE, // Đang bảo trì/sửa chữa
    DISPOSED           // Đã thanh lý/hủy bỏ
}
