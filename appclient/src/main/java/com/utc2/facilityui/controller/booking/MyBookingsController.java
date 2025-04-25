package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardBooking;
// Đảm bảo import đúng BookingResponse
import com.utc2.facilityui.response.BookingResponse;
// Import các lớp cần thiết khác
import com.utc2.facilityui.service.BookingService; // Service đã được cập nhật
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MyBookingsController implements Initializable {

    @FXML private VBox bookingListContainer; // Container để chứa các card booking
    @FXML private ScrollPane scrollPane;     // ScrollPane chứa VBox (nếu có)
    @FXML private Label statusLabel;         // Label hiển thị trạng thái tải/lỗi

    private BookingService bookingService; // Service để gọi API
    // Định dạng thời gian hiển thị trên UI
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /**
     * Phương thức này được gọi tự động sau khi FXML được tải.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bookingService = new BookingService(); // Khởi tạo service
        loadMyBookings(); // Tải danh sách booking ngay khi controller khởi tạo
    }

    /**
     * Tải danh sách booking của người dùng từ API và hiển thị chúng dưới dạng card.
     * Phiên bản này hoạt động với BookingService trả về List<BookingResponse>.
     */
    public void loadMyBookings() {
        statusLabel.setText("Đang tải danh sách booking...");
        statusLabel.setVisible(true);
        bookingListContainer.getChildren().clear();

        new Thread(() -> {
            try {
                // 1. Gọi service - Kết quả cuối cùng là List<BookingResponse>
                final List<BookingResponse> bookingList = bookingService.getMyBookings();

                Platform.runLater(() -> {
                    statusLabel.setText("");
                    statusLabel.setVisible(false);

                    if (bookingList != null && !bookingList.isEmpty()) {
                        // 2. Vòng lặp duyệt qua List<BookingResponse>
                        for (BookingResponse singleBookingResponse : bookingList) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardBooking.fxml"));
                                AnchorPane cardNode = loader.load();
                                CardBookingController cardController = loader.getController();

                                CardBooking cardBooking = new CardBooking();

                                // 3. Truy cập dữ liệu từ đối tượng BookingResponse
                                cardBooking.setBookingId(singleBookingResponse.getId());
                                cardBooking.setNameBooking(singleBookingResponse.getRoomName() != null ? singleBookingResponse.getRoomName() : "N/A");
                                cardBooking.setPurposeBooking(singleBookingResponse.getPurpose());
                                cardBooking.setPlannedStartTimeDisplay(formatDateTime(singleBookingResponse.getPlannedStartTime()));
                                cardBooking.setPlannedEndTimeDisplay(formatDateTime(singleBookingResponse.getPlannedEndTime()));
                                cardBooking.setRequestBooking(formatDateTime(singleBookingResponse.getCreatedAt()));
                                cardBooking.setStatusBooking(mapStatus(singleBookingResponse.getStatus()));

                                cardController.setBooking(cardBooking);
                                bookingListContainer.getChildren().add(cardNode);

                            } catch (IOException e) {
                                String bookingIdForError = (singleBookingResponse != null) ? singleBookingResponse.getId() : "UNKNOWN_ID";
                                System.err.println("Lỗi khi tải CardBooking.fxml cho booking ID: " + bookingIdForError + " - " + e.getMessage());
                                e.printStackTrace();
                                statusLabel.setText("Lỗi khi hiển thị một booking.");
                                statusLabel.setVisible(true);
                            } catch (Exception e) {
                                String bookingIdForError = (singleBookingResponse != null) ? singleBookingResponse.getId() : "UNKNOWN_ID";
                                System.err.println("Lỗi khi xử lý dữ liệu cho booking ID: " + bookingIdForError + " - " + e.getMessage());
                                e.printStackTrace();
                                statusLabel.setText("Lỗi dữ liệu booking.");
                                statusLabel.setVisible(true);
                            }
                        }
                    } else {
                        statusLabel.setText("Bạn chưa có yêu cầu đặt phòng nào.");
                        statusLabel.setVisible(true);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Lỗi: " + e.getMessage());
                    statusLabel.setVisible(true);
                });
                System.err.println("Lỗi khi gọi BookingService.getMyBookings(): " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Đã xảy ra lỗi không mong muốn khi tải booking.");
                    statusLabel.setVisible(true);
                });
                System.err.println("Lỗi không xác định trong luồng tải booking: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Định dạng đối tượng LocalDateTime thành chuỗi theo định dạng hiển thị mong muốn.
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            try {
                return dateTime.format(DISPLAY_FORMATTER);
            } catch (Exception e) {
                System.err.println("Lỗi định dạng thời gian: " + dateTime + " - " + e.getMessage());
                return dateTime.toString();
            }
        }
        return "N/A";
    }

    /**
     * (Tùy chọn) Ánh xạ mã trạng thái từ API sang chuỗi dễ hiểu hơn.
     */
    private String mapStatus(String status) {
        if (status == null) return "Không xác định";
        switch (status.toUpperCase()) {
            case "PENDING_APPROVAL": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Đã từ chối";
            case "CANCELLED": return "Đã hủy";
            case "COMPLETED": return "Đã hoàn thành";
            case "CHECKED_IN": return "Đã check-in";
            default: return status;
        }
    }
}