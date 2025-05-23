package com.utc2.facilityui.controller.booking; // Đảm bảo package chính xác

import com.utc2.facilityui.model.CardDefaultStatus; // Model tương ứng
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class CardDefaultStatusController {

    @FXML private Label nameBookingText;
    @FXML private Text userNameText;
    @FXML private Text purposeText;
    @FXML private Text timeRangeText;
    @FXML private Text requestBookingText;
    @FXML private HBox equipmentContainerDefault; // Để ẩn/hiện cả cụm thiết bị
    @FXML private Text equipmentsListTextDefault;
    @FXML private Text statusText;

    // MyBookingsController sẽ gọi phương thức này
    public void setData(CardDefaultStatus data) {
        if (data == null) {
            // Xử lý trường hợp data là null, ví dụ đặt tất cả text là "N/A" hoặc thông báo lỗi
            nameBookingText.setText("Lỗi: Dữ liệu không có sẵn");
            userNameText.setText("N/A");
            purposeText.setText("N/A");
            timeRangeText.setText("N/A");
            requestBookingText.setText("N/A");
            equipmentContainerDefault.setVisible(false);
            equipmentContainerDefault.setManaged(false);
            statusText.setText("N/A");
            return;
        }

        nameBookingText.setText(data.getNameBooking() != null ? data.getNameBooking() : "Chưa có thông tin");
        userNameText.setText(data.getUserName() != null ? data.getUserName() : "Không rõ");

        if (data.getPurposeBooking() != null && !data.getPurposeBooking().isEmpty()) {
            purposeText.setText(data.getPurposeBooking());
            // Nếu FXML của CardDefaultStatus không có fx:id="purposeContainer" thì không cần dòng dưới
            // purposeContainer.setVisible(true); 
            // purposeContainer.setManaged(true);
            purposeText.getParent().setVisible(true); // Giả sử purposeText nằm trong HBox
            purposeText.getParent().setManaged(true);
        } else {
            // purposeContainer.setVisible(false);
            // purposeContainer.setManaged(false);
            purposeText.getParent().setVisible(false);
            purposeText.getParent().setManaged(false);
        }

        timeRangeText.setText(data.getTimeRangeDisplay() != null ? data.getTimeRangeDisplay() : "N/A");
        requestBookingText.setText(data.getRequestBooking() != null ? data.getRequestBooking() : "N/A");

        if (data.getEquipmentsDisplay() != null && !data.getEquipmentsDisplay().isEmpty()) {
            equipmentsListTextDefault.setText(data.getEquipmentsDisplay());
            equipmentContainerDefault.setVisible(true);
            equipmentContainerDefault.setManaged(true);
        } else {
            equipmentsListTextDefault.setText("");
            equipmentContainerDefault.setVisible(false);
            equipmentContainerDefault.setManaged(false);
        }

        String apiStatus = data.getStatusBooking();
        String displayStatusText = apiStatus; // Mặc định hiển thị status gốc

        // Việt hóa và áp style cho các trạng thái phổ biến mà card này có thể hiển thị
        statusText.getStyleClass().removeAll("status-confirmed", "status-pending", "status-cancelled", "status-completed", "status-rejected", "status-default");
        if (apiStatus != null) {
            switch (apiStatus.toUpperCase()) {
                case "COMPLETED":
                    displayStatusText = "Đã hoàn thành";
                    statusText.getStyleClass().add("status-completed");
                    break;
                case "CHECKED_IN": // Ví dụ nếu có trạng thái này
                    displayStatusText = "Đã check-in";
                    statusText.getStyleClass().add("status-confirmed"); // Dùng tạm style của confirmed
                    break;
                // Thêm các case khác nếu cần
                default:
                    displayStatusText = apiStatus; // Giữ nguyên nếu không có bản dịch cụ thể
                    statusText.getStyleClass().add("status-default");
                    break;
            }
        } else {
            displayStatusText = "Chưa rõ";
            statusText.getStyleClass().add("status-default");
        }
        statusText.setText(displayStatusText);
    }
}