package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class CardBookingController {
    @FXML private Button btnCancel;
    @FXML private Text dateBooking;
    @FXML private Label nameBooking;
    @FXML private Text purposeBooking;
    @FXML private Text requestBooking;
    @FXML private Text statusBooking;
    @FXML private Text timeBooking;
//
    // Có thể thêm các @FXML khác nếu FXML có

    public void setData(CardBooking cardbooking) {
        if (cardbooking == null) return; // Thêm kiểm tra null

        nameBooking.setText(getValueOrDefault(cardbooking.getNameBooking(), "N/A"));
        purposeBooking.setText(getValueOrDefault(cardbooking.getPurposeBooking(), ""));
        dateBooking.setText(getValueOrDefault(cardbooking.getDateBooking(), "N/A"));
        timeBooking.setText(getValueOrDefault(cardbooking.getTimeBooking(), "N/A"));
        requestBooking.setText(getValueOrDefault(cardbooking.getRequestBooking(), "N/A"));
        statusBooking.setText(getValueOrDefault(cardbooking.getStatusBooking(), "N/A"));

        // Cập nhật trạng thái/hiển thị của nút Cancel dựa trên statusBooking nếu cần
        // Ví dụ: chỉ cho hủy khi đang PENDING hoặc APPROVED
        // boolean canCancel = "Chờ duyệt".equals(statusBooking.getText()) || "Đã duyệt".equals(statusBooking.getText());
        // btnCancel.setDisable(!canCancel);
    }

    // Hàm tiện ích (có thể đưa vào lớp Utils)
    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    // Getter cho nút Cancel để MyBookingController có thể gán hành động
    public Button getBtnCancel() {
        return btnCancel;
    }
}