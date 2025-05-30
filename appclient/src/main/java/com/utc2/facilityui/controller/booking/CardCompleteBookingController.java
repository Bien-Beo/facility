package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardCompleteBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime; // Thêm import này
import java.time.format.DateTimeFormatter; // Thêm import này

public class CardCompleteBookingController {

    // --- Các thành phần FXML từ cardCompleteBooking.fxml ---
    @FXML private Label nameBookingLabel;
    @FXML private Text userNameText;
    @FXML private Text purposeBookingText;
    @FXML private Text timeRangeText;
    @FXML private Text requestBookingText;
    @FXML private Text statusBookingText;
    @FXML private Text actualCheckInTime;
    @FXML private Text actualCheckOutTime;
    @FXML private HBox equipmentContainer;
    @FXML private Text equipmentsListText;
    @FXML private HBox purposeContainer;
    @FXML private HBox userContainer;
    @FXML private HBox userContainer1;

    // Biến lưu trữ dữ liệu cho card này
    private CardCompleteBooking bookingData;

    // Định dạng thời gian hiển thị (tương tự như trong CardAcceptBookingController)
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");

    public void setCompleteBookingData(CardCompleteBooking booking) {
        this.bookingData = booking;
        updateCardUI();
    }

    private void updateCardUI() {
        if (bookingData != null) {
            nameBookingLabel.setText(bookingData.getNameBooking() != null ? bookingData.getNameBooking() : "Không có tên phòng");
            userNameText.setText(bookingData.getUserName() != null ? bookingData.getUserName() : "Không rõ");
            timeRangeText.setText(bookingData.getTimeRangeDisplay() != null ? bookingData.getTimeRangeDisplay() : "N/A");
            requestBookingText.setText(bookingData.getRequestBooking() != null ? bookingData.getRequestBooking() : "N/A");

            boolean hasPurpose = bookingData.getPurposeBooking() != null && !bookingData.getPurposeBooking().trim().isEmpty();
            purposeBookingText.setText(hasPurpose ? bookingData.getPurposeBooking() : "");
            if (userContainer1 != null) { // userContainer1 là HBox chứa purposeBookingText
                userContainer1.setVisible(hasPurpose);
                userContainer1.setManaged(hasPurpose);
            }
            // Đảm bảo purposeBookingText cũng được quản lý visibility nếu nó nằm ngoài userContainer1
            // Hoặc nếu userContainer1 là parent trực tiếp thì không cần set riêng cho purposeBookingText.
            // purposeBookingText.setVisible(hasPurpose);
            // purposeBookingText.setManaged(hasPurpose);


            String equipments = bookingData.getEquipmentsDisplay();
            boolean hasEquipments = equipments != null && !equipments.trim().isEmpty();
            equipmentsListText.setText(hasEquipments ? equipments : "");
            if (equipmentContainer != null) {
                equipmentContainer.setVisible(hasEquipments);
                equipmentContainer.setManaged(hasEquipments);
            }


            statusBookingText.setText("Đã hoàn thành");
            statusBookingText.getStyleClass().clear();
            statusBookingText.getStyleClass().add("status-completed");

            // Sử dụng formatter để chuyển LocalDateTime thành String
            actualCheckInTime.setText(
                    bookingData.getActualCheckInTime() != null
                            ? bookingData.getActualCheckInTime().format(DISPLAY_TIME_FORMATTER) // Trả về String
                            : "Chưa check-in" // Trả về String
            );

            actualCheckOutTime.setText(
                    bookingData.getActualCheckOutTime() != null
                            ? bookingData.getActualCheckOutTime().format(DISPLAY_TIME_FORMATTER) // Trả về String
                            : "Chưa check-out" // Trả về String
            );
            // ======================

        } else {
            nameBookingLabel.setText("Lỗi dữ liệu");
            userNameText.setText("N/A");
            purposeBookingText.setText("");
            if (userContainer1 != null) { userContainer1.setVisible(false); userContainer1.setManaged(false); }
            timeRangeText.setText("N/A");
            requestBookingText.setText("N/A");
            equipmentsListText.setText("");
            if (equipmentContainer != null) { equipmentContainer.setVisible(false); equipmentContainer.setManaged(false); }
            statusBookingText.setText("N/A"); statusBookingText.getStyleClass().clear();
            actualCheckInTime.setText("N/A");
            actualCheckOutTime.setText("N/A");
        }
    }
}