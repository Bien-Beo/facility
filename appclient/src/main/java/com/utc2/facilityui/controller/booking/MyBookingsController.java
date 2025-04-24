package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.response.BookingResponse;
import com.utc2.facilityui.model.CardBooking;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Giả sử dùng ScrollPane
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox; // Giả sử dùng VBox chứa card

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MyBookingsController implements Initializable {
//
    @FXML private VBox bookingListContainer; // fx:id của VBox chứa các card booking
    @FXML private ScrollPane scrollPane; // fx:id của ScrollPane bao ngoài (nếu có)
    @FXML private Label statusLabel; // Label để hiển thị trạng thái loading/lỗi

    private BookingService bookingService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bookingService = new BookingService();
        loadMyBookings(); // Tải danh sách booking khi khởi tạo
    }

    @FXML // Có thể thêm nút refresh và gọi hàm này
    public void refreshBookings() {
        loadMyBookings();
    }

    private void loadMyBookings() {
        if (bookingListContainer == null) return;
        bookingListContainer.getChildren().clear(); // Xóa card cũ
        showStatus("Đang tải danh sách booking...");

        new Thread(() -> {
            try {
                List<BookingResponse> myBookings = bookingService.getMyBookings();

                Platform.runLater(() -> {
                    showStatus(""); // Xóa thông báo loading
                    if (myBookings == null || myBookings.isEmpty()) {
                        showStatus("Bạn chưa có booking nào.");
                        return;
                    }

                    for (BookingResponse booking : myBookings) {
                        if (booking == null) continue;
                        try {
                            CardBooking cardData = mapBookingResponseToCardBooking(booking);
                            if (cardData == null) continue;

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardBooking.fxml"));
                            AnchorPane cardNode = loader.load();

                            CardBookingController cardController = loader.getController();
                            if (cardController != null) {
                                cardController.setData(cardData);
                                // Có thể thêm hành động cho nút Cancel trên card ở đây
                                // cardController.getBtnCancel().setOnAction(e -> handleCancelBooking(booking.getId()));
                            }
                            bookingListContainer.getChildren().add(cardNode);

                        } catch (IOException e) {
                            System.err.println("Lỗi load cardBooking.fxml: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Lỗi xử lý booking ID " + booking.getId() + ": " + e.getMessage());
                        }
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> showStatus("Lỗi tải danh sách booking: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm ánh xạ BookingResponse sang CardBooking
    private CardBooking mapBookingResponseToCardBooking(BookingResponse booking) {
        CardBooking card = new CardBooking();

        // Xử lý tên hiển thị (ví dụ: Tên phòng - Mục đích)
        String name = (booking.getRoomName() != null ? booking.getRoomName() : "ID: " + booking.getRoomId());
        card.setNameBooking(name);
        card.setPurposeBooking(getValueOrDefault(booking.getPurpose(),"Không có mục đích"));

        // Format ngày giờ
        card.setDateBooking(formatDisplayDate(booking.getPlannedStartTime()));
        card.setTimeBooking(formatDisplayTimeRange(booking.getPlannedStartTime(), booking.getPlannedEndTime()));
        card.setRequestBooking(formatDisplayDateTime(booking.getCreatedAt())); // Ngày yêu cầu
        card.setStatusBooking(formatDisplayStatus(booking.getStatus())); // Trạng thái

        return card;
    }

    // --- Các hàm helper format hiển thị ---
    private String formatDisplayDate(String isoDateTime) {
        if (isoDateTime == null) return "N/A";
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // Format theo ý muốn, ví dụ: "Thứ 5, 12 tháng 4 2025" hoặc "12/04/2025"
            return dateTime.format(DateTimeFormatter.ofPattern("EEE, dd MMMM yyyy"));
        } catch (Exception e) { return isoDateTime; }
    }
    private String formatDisplayTimeRange(String isoStart, String isoEnd) {
        if (isoStart == null || isoEnd == null) return "N/A";
        try {
            LocalDateTime start = LocalDateTime.parse(isoStart, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(isoEnd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // Format theo ý muốn, ví dụ: "09:00 - 11:30"
            return start.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + end.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) { return "Invalid time"; }
    }
    private String formatDisplayDateTime(String isoDateTime) {
        if (isoDateTime == null) return "N/A";
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // Format theo ý muốn, ví dụ: "10:30 12/04/2025"
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
        } catch (Exception e) { return isoDateTime; }
    }
    private String formatDisplayStatus(String status) {
        if (status == null) return "N/A";
        // Có thể Việt hóa hoặc trả về nguyên gốc
        switch (status.toUpperCase()) {
            case "PENDING": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Bị từ chối";
            case "CANCELLED": return "Đã hủy";
            case "COMPLETED": return "Đã hoàn thành";
            default: return status;
        }
    }
    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    // Hiển thị trạng thái loading/lỗi
    private void showStatus(String message) {
        if (statusLabel != null) {
            if (message == null || message.isEmpty()) {
                statusLabel.setVisible(false);
            } else {
                statusLabel.setText(message);
                statusLabel.setVisible(true);
            }
        }
        // Nếu không có statusLabel, có thể thêm Label tạm thời vào container
        else if (bookingListContainer != null && message != null && !message.isEmpty()) {
            bookingListContainer.getChildren().add(new Label(message));
        }
    }

    // Hàm xử lý hủy booking (ví dụ)
    private void handleCancelBooking(String bookingId) {
        System.out.println("Yêu cầu hủy booking ID: " + bookingId);
        // Gọi BookingService.cancelBooking(bookingId)
        // Nếu thành công thì gọi refreshBookings()
    }
}