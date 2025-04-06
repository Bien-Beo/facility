package com.utc2.facility.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationRequest {

    String roomId;

    @NotBlank(message = "Mục đích không được để trống")
    String purpose;

    @NotNull(message = "Thời gian bắt đầu dự kiến không được để trống")
    @FutureOrPresent(message = "Thời gian bắt đầu dự kiến phải là hiện tại hoặc tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Đảm bảo format đúng nếu cần
    LocalDateTime plannedStartTime; // Thời gian dự kiến bắt đầu

    @NotNull(message = "Thời gian kết thúc dự kiến không được để trống")
    @Future(message = "Thời gian kết thúc dự kiến phải ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime plannedEndTime; // Thời gian dự kiến kết thúc

    // Danh sách ID của các thiết bị cụ thể (EquipmentItem) muốn mượn THÊM
    // (Không bao gồm các thiết bị mặc định của phòng - hệ thống sẽ tự thêm chúng)
    List<String> additionalEquipmentItemIds;

    // Ghi chú thêm từ người dùng (tùy chọn)
    String note;

    // ----- Validation Logic -----
    // Kiểm tra đảm bảo thời gian kết thúc phải sau thời gian bắt đầu
    @AssertTrue(message = "Thời gian kết thúc dự kiến phải sau thời gian bắt đầu dự kiến")
    private boolean isEndTimeAfterStartTime() {
        // Chỉ kiểm tra nếu cả hai thời gian đều không null (NotNull đã kiểm tra null riêng lẻ)
        if (plannedStartTime == null || plannedEndTime == null) {
            return true; // Để các validation @NotNull khác xử lý
        }
        return plannedEndTime.isAfter(plannedStartTime);
    }
}