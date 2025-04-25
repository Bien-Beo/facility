package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text; // Sử dụng Text cho nội dung dài có thể xuống dòng
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CardBookingController {

    // --- Khai báo @FXML theo yêu cầu mới ---
    @FXML private Button btnCancel;
    @FXML private Label nameBooking;
    @FXML private Text plannedEndTime;   // Hiển thị thời gian kết thúc
    @FXML private Text plannedStartTime; // Hiển thị thời gian bắt đầu
    @FXML private Text purposeBooking;
    @FXML private Text requestBooking;   // Thời gian gửi yêu cầu
    @FXML private Text statusBooking;
    // --- Kết thúc khai báo @FXML ---

    private CardBooking booking;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public void setBooking(CardBooking booking) {
        this.booking = booking;
        updateCard();
    }

    private void updateCard() {
        if (booking != null) {
            nameBooking.setText("Phòng: " + booking.getNameBooking()); // Bạn có thể cần thông tin tên phòng từ BookingResponse
            purposeBooking.setText("Mục đích: " + booking.getPurposeBooking());
            plannedStartTime.setText("Bắt đầu: " + booking.getPlannedStartTimeDisplay());
            plannedEndTime.setText("Kết thúc: " + booking.getPlannedEndTimeDisplay());
            requestBooking.setText("Yêu cầu lúc: " + booking.getRequestBooking()); // Bạn cần set giá trị này khi tạo CardBooking
            statusBooking.setText("Trạng thái: " + booking.getStatusBooking());
        }
    }

    // Xử lý sự kiện khi nút hủy được bấm (nếu cần)
    @FXML
    private void handleCancelBooking() {
        // Thêm logic hủy booking ở đây, có thể cần gọi service
        System.out.println("Hủy booking với ID: " + booking.getBookingId());
        // Sau khi hủy thành công, có thể cần cập nhật lại danh sách booking
    }
}