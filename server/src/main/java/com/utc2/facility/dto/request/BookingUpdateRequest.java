package com.utc2.facility.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
public class BookingUpdateRequest {

    // Các trường này là optional, chỉ cập nhật nếu được cung cấp giá trị trong request

    String purpose; // Mục đích mới

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

    String note; // Ghi chú mới

    // Lưu ý về Validation thời gian:
    // Việc kiểm tra plannedEndTime > plannedStartTime khi cập nhật phức tạp hơn
    // vì phải so sánh với giá trị hiện có nếu chỉ một trong hai thời gian được cập nhật.
    // Validation này nên được thực hiện ở tầng Service nơi có đủ thông tin.
    // Có thể giữ @Future/@FutureOrPresent nếu muốn kiểm tra đơn giản ở DTO.
}