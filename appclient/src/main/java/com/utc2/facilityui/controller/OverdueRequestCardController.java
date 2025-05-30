package com.utc2.facilityui.controller;

import com.utc2.facilityui.response.BookingResponse; // Sử dụng BookingResponse
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox; // Thẻ gốc của FXML bạn cung cấp

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class OverdueRequestCardController implements Initializable {

    // --- Các @FXML khớp với fx:id trong cardOverdueRequest.fxml bạn cung cấp ---
    @FXML private HBox approvalDetailCard; // fx:id của HBox gốc
    @FXML private Label roomNameLabel;
    @FXML private Label bookedByUserLabel;
    @FXML private Label purposeBookingLabel; // FXML dùng fx:id này
    @FXML private Label timeRangeLabel;    // FXML dùng fx:id này (cho Thời gian đặt)
    @FXML private Label requestedAtLabel;   // FXML dùng fx:id này (cho Yêu cầu hủy/Yêu cầu lúc)
    @FXML private Label cancellationReason; // FXML dùng fx:id này (cho Lý do)
    @FXML private Label statusLabel;

    @FXML private TextArea reasonTextArea; // FXML dùng fx:id này (cho Thông báo thu hồi)
    @FXML private Button btnRemind;          // FXML dùng fx:id này (sẽ đặt text là NHẮC NHỞ)
    @FXML private Button btnRevoke;          // FXML dùng fx:id này (sẽ đặt text là THU HỒI)

    private BookingResponse currentBooking;

    // Định dạng ngày giờ Việt Nam
    private static final DateTimeFormatter VNF_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", new Locale("vi", "VN"));
    private static final DateTimeFormatter VNF_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("vi", "VN"));
    // Formatter cho hiển thị đầy đủ ngày giờ, ví dụ: 07:00, Th 4, 07/05/2025
    private static final DateTimeFormatter VNF_FULL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, EEE dd/MM/yyyy", new Locale("vi", "VN"));


    // Properties cho actions sẽ được controller cha gán
    private final ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRemindAction = new SimpleObjectProperty<>();
    private final ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRevokeAction = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (approvalDetailCard != null) { // Sử dụng fx:id từ FXML của bạn
            approvalDetailCard.setUserData(this);
        } else {
            System.err.println("LỖI: approvalDetailCard (HBox gốc) là null. Kiểm tra fx:id trong FXML card.");
        }

        // Gán sự kiện cho các nút
        // Text và style của nút sẽ được FXML định nghĩa, ở đây chỉ gán action
        if (btnRemind != null) {
            // btnRemind.setText("NHẮC NHỞ"); // Text đã có trong FXML
            btnRemind.setOnAction(event -> {
                if (onRemindAction.get() != null) onRemindAction.get().handle(event);
            });
        }
        if (btnRevoke != null) {
            // btnRevoke.setText("THU HỒI"); // Text đã có trong FXML
            btnRevoke.setOnAction(event -> {
                if (onRevokeAction.get() != null) onRevokeAction.get().handle(event);
            });
        }
        if(reasonTextArea != null) { // fx:id từ FXML của bạn
            reasonTextArea.setPromptText("Nội dung nhắc nhở/Lý do thu hồi (nếu có)");
        }
    }

    public void setData(BookingResponse booking) {
        this.currentBooking = booking;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setDataInternal(booking));
            return;
        }
        setDataInternal(booking);
    }

    private void setDataInternal(BookingResponse bookingData) {
        if (bookingData == null) {
            clearLabels();
            return;
        }

        if (roomNameLabel != null) roomNameLabel.setText("Phòng: " + getStringOrEmpty(bookingData.getRoomName()));
        if (bookedByUserLabel != null) bookedByUserLabel.setText(getStringOrEmpty(bookingData.getUserName())); // FXML đã có label "Người đặt:"
        if (purposeBookingLabel != null) purposeBookingLabel.setText(getStringOrEmpty(bookingData.getPurpose())); // FXML đã có label "Mục đích:"

        // Hiển thị "Thời gian đặt:"
        String displayPlannedTime = formatDateTimeRange(bookingData.getPlannedStartTime(), bookingData.getPlannedEndTime());
        if (timeRangeLabel != null) timeRangeLabel.setText(displayPlannedTime); // FXML đã có label "Thời gian đặt:"

        // Hiển thị "Yêu cầu lúc:" (thời điểm tạo booking)
        // FXML của bạn có label tĩnh "Yêu cầu hủy:", sẽ được ghi đè bởi setText
        String requestedAtText = "N/A";
        if (bookingData.getCreatedAt() != null) {
            requestedAtText = bookingData.getCreatedAt().format(VNF_FULL_DATETIME_FORMATTER);
        }
        if (requestedAtLabel != null) {
            // Label tĩnh trong FXML là "Yêu cầu hủy:", nhưng ta sẽ đặt giá trị thời gian yêu cầu đặt phòng
            // Nếu muốn thay đổi cả nhãn, cần làm ở controller cha hoặc FXML template khác.
            // Hiện tại chỉ cập nhật giá trị.
            requestedAtLabel.setText(requestedAtText);
        }

        // Hiển thị "Đến hạn trả:" (plannedEndTime) cho label có fx:id="cancellationReason"
        // FXML của bạn có label tĩnh "Lý do:", sẽ được ghi đè bởi setText
        String overdueSinceText = "N/A";
        if (bookingData.getPlannedEndTime() != null) {
            overdueSinceText = bookingData.getPlannedEndTime().format(VNF_FULL_DATETIME_FORMATTER);
        }
        if (cancellationReason != null) { // fx:id="cancellationReason" trong FXML của bạn
            cancellationReason.setText(overdueSinceText);
        }


        if (statusLabel != null) {
            String statusDisplay = translateBookingStatus(bookingData.getStatus());
            statusLabel.setText(statusDisplay); // FXML đã có text "Quá hạn", sẽ được ghi đè
            updateStatusLabelStyle(bookingData.getStatus());
        }
        if (reasonTextArea != null) reasonTextArea.clear();
    }

    private void clearLabels() {
        if(roomNameLabel != null) roomNameLabel.setText("Phòng: N/A");
        if(bookedByUserLabel != null) bookedByUserLabel.setText("N/A");
        if(purposeBookingLabel != null) purposeBookingLabel.setText("N/A");
        if(timeRangeLabel != null) timeRangeLabel.setText("N/A");
        if(requestedAtLabel != null) requestedAtLabel.setText("N/A");
        if(cancellationReason != null) cancellationReason.setText("N/A");
        if(statusLabel != null) {
            statusLabel.setText("N/A");
            updateStatusLabelStyle(null);
        }
        if(reasonTextArea != null) reasonTextArea.clear();
        if(btnRemind != null) btnRemind.setDisable(false);
        if(btnRevoke != null) btnRevoke.setDisable(false);
    }

    private String formatDateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "N/A";
        // Format: 07:00 - 11:30, Th 4, 07/05/2025 (như trong image_b2180f.png)
        if (start.toLocalDate().equals(end.toLocalDate())) {
            return start.format(VNF_TIME_FORMATTER) + " - " + end.format(VNF_TIME_FORMATTER) + ", " + start.format(VNF_DATE_FORMATTER);
        } else {
            // Nếu khác ngày, hiển thị đầy đủ cho cả hai
            return start.format(VNF_FULL_DATETIME_FORMATTER) + " đến " + end.format(VNF_FULL_DATETIME_FORMATTER);
        }
    }

    private String translateBookingStatus(String statusKey) {
        if (statusKey == null) return "N/A";
        // Chỉ cần xử lý trạng thái "OVERDUE" cho card này
        if ("OVERDUE".equalsIgnoreCase(statusKey)) {
            return "QUÁ HẠN";
        }
        return statusKey; // Hoặc một giá trị mặc định khác nếu không phải OVERDUE
    }

    private void updateStatusLabelStyle(String statusKey) {
        if (statusLabel == null) return;
        String styleBackgroundColor = "#757575"; // Màu xám mặc định
        String styleTextColor = "white";
        if (statusKey != null) {
            if ("OVERDUE".equalsIgnoreCase(statusKey)) {
                styleBackgroundColor = "#d32f2f"; // Màu đỏ đậm cho quá hạn
            }
            // Thêm các case khác nếu card này có thể hiển thị nhiều trạng thái (hiện tại chỉ tập trung vào OVERDUE)
        }
        statusLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-color: " + styleBackgroundColor + "; " +
                        "-fx-text-fill: " + styleTextColor + "; " +
                        "-fx-padding: 3px 8px; " +
                        "-fx-background-radius: 20px;"
        );
    }

    // Action Properties
    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRemindActionProperty() { return onRemindAction;}
    public void setOnRemindAction(javafx.event.EventHandler<ActionEvent> handler) { this.onRemindAction.set(handler);}
    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRevokeActionProperty() { return onRevokeAction;}
    public void setOnRevokeAction(javafx.event.EventHandler<ActionEvent> handler) { this.onRevokeAction.set(handler);}

    // Getters cho nút và lý do (sử dụng fx:id từ FXML bạn cung cấp)
    public Button getRemindButton() { return btnRemind; } // FXML là btnRemind
    public Button getRevokeButton() { return btnRevoke; }  // FXML là btnRevoke
    public String getActionReason() { return reasonTextArea != null ? reasonTextArea.getText().trim() : ""; } // FXML là reasonTextArea

    private String getStringOrEmpty(String str) { return str != null && !str.isBlank() ? str : "N/A"; }
}