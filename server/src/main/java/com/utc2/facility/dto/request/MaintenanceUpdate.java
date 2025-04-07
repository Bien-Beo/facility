package com.utc2.facility.dto.request;

import com.utc2.facility.enums.MaintenanceStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaintenanceUpdate {

    String actionTaken; // Công việc đã làm
    LocalDateTime startDate; // Ngày bắt đầu (nếu chưa có)
    LocalDateTime completionDate; // Ngày hoàn thành (nếu xong)
    MaintenanceStatus status; // Cập nhật trạng thái mới
    BigDecimal cost; // Chi phí
    String notes; // Ghi chú thêm

}
