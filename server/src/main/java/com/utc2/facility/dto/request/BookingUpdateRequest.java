package com.utc2.facility.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
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
public class BookingUpdateRequest { // Dùng để cập nhật booking đang PENDING

    String purpose; // Mục đích mới

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    String note;

    @FutureOrPresent(message = "Thời gian bắt đầu dự kiến phải là hiện tại hoặc tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime plannedStartTime; // Thời gian bắt đầu mới

    @Future(message = "Thời gian kết thúc dự kiến phải ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime plannedEndTime; // Thời gian kết thúc mới

    // Danh sách ĐẦY ĐỦ các ID thiết bị muốn mượn THÊM sau khi cập nhật.
    // Backend sẽ so sánh với danh sách hiện tại để thêm/xóa các liên kết cần thiết.
    // Gửi danh sách rỗng nếu muốn xóa hết thiết bị mượn thêm.
    // Gửi null nếu không muốn thay đổi danh sách thiết bị mượn thêm.
    List<String> additionalEquipmentItemIds;

    // Validation: Kiểm tra end > start nếu CẢ HAI thời gian đều được gửi lên trong request update
    @AssertTrue(message = "Thời gian kết thúc dự kiến phải sau thời gian bắt đầu dự kiến")
    private boolean isEndTimeAfterStartTimeIfBothProvided() {
        if (plannedStartTime != null && plannedEndTime != null) {
            return plannedEndTime.isAfter(plannedStartTime);
        }
        return true;
    }
}