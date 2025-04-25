package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.Result;
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
    @FXML private VBox bookingListContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label statusLabel;

    private BookingService bookingService;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bookingService = new BookingService();
        loadMyBookings();
    }

    public void loadMyBookings() {
        statusLabel.setText("Đang tải...");
        bookingListContainer.getChildren().clear(); // Xóa các card cũ

        new Thread(() -> {
            try {
                List<Result<BookingResponse>> bookings = bookingService.getMyBookings();
                Platform.runLater(() -> {
                    statusLabel.setText("");
                    if (bookings != null && !bookings.isEmpty()) {
                        for (BookingResponse bookingResponse : bookings) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/fxml/booking/CardBooking.fxml"));
                                AnchorPane cardNode = loader.load();
                                CardBookingController cardController = loader.getController();

                                CardBooking cardBooking = new CardBooking();
                                cardBooking.setBookingId(bookingResponse.getId());
                                cardBooking.setNameBooking(bookingResponse.getRoomName() != null ? bookingResponse.getRoomName() : bookingResponse.getRoomId());
                                cardBooking.setPurposeBooking(bookingResponse.getPurpose());
                                cardBooking.setPlannedStartTimeDisplay(formatDateTime(bookingResponse.getPlannedStartTime().toString()));
                                cardBooking.setPlannedEndTimeDisplay(formatDateTime(bookingResponse.getPlannedEndTime().toString()));
                                cardBooking.setRequestBooking(formatDateTime(bookingResponse.getCreatedAt().toString()));
                                cardBooking.setStatusBooking(bookingResponse.getStatus());

                                cardController.setBooking(cardBooking);
                                bookingListContainer.getChildren().add(cardNode);
                            } catch (IOException e) {
                                Platform.runLater(() -> {
                                    statusLabel.setText("Lỗi khi tải card booking: " + e.getMessage());
                                });
                                e.printStackTrace();
                            }
                        }
                    } else {
                        statusLabel.setText("Không có booking nào.");
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Lỗi khi tải danh sách booking: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime != null && !isoDateTime.isEmpty()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, ISO_FORMATTER);
                return dateTime.format(DISPLAY_FORMATTER);
            } catch (Exception e) {
                System.err.println("Lỗi format thời gian: " + isoDateTime + " - " + e.getMessage());
                return isoDateTime; // Trả về nguyên bản nếu lỗi
            }
        }
        return "";
    }
}