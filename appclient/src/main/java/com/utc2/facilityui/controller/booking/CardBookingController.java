package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardBooking;
// import com.utc2.facilityui.service.BookingService; // Import nếu cần gọi service khi hủy
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class CardBookingController {

    // --- Khai báo @FXML khớp với CardBooking.fxml ---
    @FXML private Label nameBooking;        // Label hiển thị tên phòng/booking
    @FXML private Text purposeBooking;      // Text hiển thị mục đích
    @FXML private Text plannedStartTime;    // Text hiển thị thời gian bắt đầu
    @FXML private Text plannedEndTime;      // Text hiển thị thời gian kết thúc
    @FXML private Text requestBooking;      // Text hiển thị thời gian yêu cầu
    @FXML private Text statusBooking;       // Text hiển thị trạng thái
    @FXML private Button btnCancel;         // Button hủy

    // Có thể thêm các @FXML khác nếu cần hiển thị thêm thông tin từ FXML
    // Ví dụ: @FXML private Label approvedByLabel;
    //        @FXML private Text approvedByText;

    private CardBooking booking; // Model chứa dữ liệu cho card này
    // private BookingService bookingService; // Khởi tạo nếu cần gọi khi hủy

    public void setBooking(CardBooking booking) {
        this.booking = booking;
        // this.bookingService = new BookingService(); // Khởi tạo service nếu cần
        updateCard();
    }

    /**
     * Cập nhật các thành phần UI trên card với dữ liệu từ model `booking`.
     */
    private void updateCard() {
        if (booking != null) {
            // Cập nhật các Text và Label với dữ liệu từ CardBooking model
            // Thêm tiền tố để rõ ràng hơn cho người dùng
            nameBooking.setText(booking.getNameBooking() != null ? booking.getNameBooking() : "Chưa có tên");
            purposeBooking.setText(booking.getPurposeBooking() != null ? booking.getPurposeBooking() : "Không có mục đích");
            plannedStartTime.setText(booking.getPlannedStartTimeDisplay() != null ? booking.getPlannedStartTimeDisplay() : "N/A");
            plannedEndTime.setText(booking.getPlannedEndTimeDisplay() != null ? booking.getPlannedEndTimeDisplay() : "N/A");
            requestBooking.setText(booking.getRequestBooking() != null ? booking.getRequestBooking() : "N/A");
            statusBooking.setText(booking.getStatusBooking() != null ? booking.getStatusBooking() : "N/A");

            // --- (Tùy chọn) Ẩn/hiện nút Hủy dựa trên trạng thái ---
            // Ví dụ: Chỉ cho phép hủy nếu trạng thái là "Chờ duyệt" (PENDING)
            boolean canCancel = "Chờ duyệt".equalsIgnoreCase(booking.getStatusBooking()) || "Đã duyệt".equalsIgnoreCase(booking.getStatusBooking()); // Hoặc các trạng thái khác cho phép hủy
            btnCancel.setVisible(canCancel);
            btnCancel.setManaged(canCancel); // Đảm bảo không chiếm không gian nếu ẩn

        } else {
            // Xử lý trường hợp booking là null (dù không nên xảy ra nếu logic đúng)
            nameBooking.setText("Lỗi dữ liệu");
            purposeBooking.setText("");
            plannedStartTime.setText("");
            plannedEndTime.setText("");
            requestBooking.setText("");
            statusBooking.setText("");
            btnCancel.setVisible(false);
            btnCancel.setManaged(false);
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Cancel".
     */
    @FXML
    private void handleCancelBooking() {
        if (booking != null && booking.getBookingId() != null) {
            System.out.println("Yêu cầu hủy booking với ID: " + booking.getBookingId());

            // --- (Triển khai logic hủy) ---
            // 1. Hiển thị hộp thoại xác nhận hỏi người dùng có chắc chắn muốn hủy không.
            //    Ví dụ dùng Alert:
            //    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            //    confirmation.setTitle("Xác nhận hủy");
            //    confirmation.setHeaderText("Bạn có chắc chắn muốn hủy yêu cầu đặt phòng này?");
            //    confirmation.setContentText("Phòng: " + booking.getNameBooking() + "\nThời gian: " + booking.getPlannedStartTimeDisplay());
            //    Optional<ButtonType> result = confirmation.showAndWait();

            //    if (result.isPresent() && result.get() == ButtonType.OK) {
            //        // 2. Gọi BookingService để thực hiện hủy trên backend
            //        // new Thread(() -> {
            //        //     try {
            //        //         boolean success = bookingService.cancelBooking(booking.getBookingId(), "Lý do hủy từ người dùng..."); // Cần có ô nhập lý do hoặc lý do mặc định
            //        //         Platform.runLater(() -> {
            //        //             if (success) {
            //        //                 // 3. Cập nhật lại giao diện (ví dụ: đổi trạng thái, làm mờ card, hoặc tải lại toàn bộ danh sách)
            //        //                 statusBooking.setText("Đã hủy");
            //        //                 btnCancel.setVisible(false);
            //        //                 btnCancel.setManaged(false);
            //        //                 // Hoặc gọi phương thức trong MyBookingsController để tải lại:
            //        //                 // ((MyBookingsController) nameBooking.getScene().lookup("#myBookingsControllerRootNodeId")).loadMyBookings(); // Cần có ID cho node gốc của MyBookings
            //        //                 System.out.println("Hủy thành công booking ID: " + booking.getBookingId());
            //        //             } else {
            //        //                 // Hiển thị thông báo lỗi hủy
            //        //                 Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            //        //                 errorAlert.setTitle("Lỗi hủy");
            //        //                 errorAlert.setHeaderText("Không thể hủy yêu cầu đặt phòng.");
            //        //                 errorAlert.setContentText("Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
            //        //                 errorAlert.showAndWait();
            //        //             }
            //        //         });
            //        //     } catch (IOException e) {
            //        //         Platform.runLater(() -> { /* Hiển thị lỗi kết nối */ });
            //        //         e.printStackTrace();
            //        //     }
            //        // }).start();
            //    }
            // --- (Kết thúc triển khai logic hủy) ---

            // Tạm thời chỉ in ra console
            System.out.println("-> Logic hủy cho booking ID " + booking.getBookingId() + " cần được triển khai.");

        } else {
            System.err.println("Không thể hủy: Thông tin booking không hợp lệ.");
        }
    }
}