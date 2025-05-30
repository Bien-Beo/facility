package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardCancelBooking;
import com.utc2.facilityui.model.CardRejectBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class CardCancelBookingController {
    @FXML
    private HBox cancelContainer;

    @FXML
    private Text cancellationReason;

    @FXML
    private Text cancelledByUserName;

    @FXML
    private HBox equipmentContainer;

    @FXML
    private Text equipmentsListText;

    @FXML
    private Label equipmentsStaticLabel;

    @FXML
    private Label nameBookingLabel;

    @FXML
    private Text purposeBookingText;

    @FXML
    private HBox purposeContainer;

    @FXML
    private HBox reasonContainer;

    @FXML
    private Text requestBookingText;

    @FXML
    private Text statusBookingText;

    @FXML
    private Text timeRangeText;

    @FXML
    private HBox userContainer;

    @FXML
    private HBox userContainer1;

    @FXML
    private Text userNameText;
    private CardCancelBooking booking;
    public void setCancelBooking(CardCancelBooking booking) {
        this.booking = booking;
        updateCardUI();
    }

    private void updateCardUI() {
        if (booking != null) {
            nameBookingLabel.setText(booking.getNameBooking() != null ? booking.getNameBooking() :"Chưa có tên phòng");

            // Hiển thị Mục đích
            if (booking.getPurposeBooking() != null && !booking.getPurposeBooking().isEmpty()) {
                purposeBookingText.setText(booking.getPurposeBooking());
                if (purposeContainer != null) { // HBox chứa name & purpose
                    purposeContainer.setVisible(true);
                    purposeContainer.setManaged(true);
                }
                // Đảm bảo purposeBookingText cũng được quản lý riêng nếu cần
                purposeBookingText.setVisible(true);
                purposeBookingText.setManaged(true);

            } else {
                purposeBookingText.setText("");
                purposeBookingText.setVisible(false);
                purposeBookingText.setManaged(false);
                // Nếu bạn muốn ẩn cả HBox purposeContainer khi không có purpose
                // (nhưng nó cũng chứa nameBookingLabel nên có thể không hợp lý)
                // if (purposeContainer != null) {
                //     purposeContainer.setVisible(false); // Cân nhắc nếu nameBookingLabel cũng trong đó
                //     purposeContainer.setManaged(false);
                // }
            }

            userNameText.setText(booking.getUserName() != null ? booking.getUserName() : "Không rõ");
            // userContainer đã visible và managed trong FXML, không cần set lại trừ khi muốn ẩn

            timeRangeText.setText(booking.getTimeRangeDisplay() != null ? booking.getTimeRangeDisplay() : "N/A");
            requestBookingText.setText(booking.getRequestBooking() != null ? booking.getRequestBooking() : "N/A");

            // Hiển thị thiết bị
            if (booking.getEquipmentsDisplay() != null && !booking.getEquipmentsDisplay().isEmpty()) {
                equipmentsListText.setText(booking.getEquipmentsDisplay());
                equipmentContainer.setVisible(true);
                equipmentContainer.setManaged(true);
            } else {
                equipmentsListText.setText("");
                equipmentContainer.setVisible(false);
                equipmentContainer.setManaged(false);
            }

            // Hiển thị thông tin người xử lý (người hủy/người từ chối)
            String actorName = booking.getCancelledByUserName();
            if (cancelContainer != null) { // Kiểm tra null cho HBox
                if (actorName != null && !actorName.isEmpty()) {
                    cancelledByUserName.setText(actorName);
                    cancelContainer.setVisible(true);
                    cancelContainer.setManaged(true);
                } else {
                    cancelledByUserName.setText(""); // Hoặc "Không có thông tin"
                    cancelContainer.setVisible(false);
                    cancelContainer.setManaged(false);
                }
            }


            // Hiển thị lý do hủy/từ chối - BỎ COMMENT NẾU FXML CÓ CÁC fx:id NÀY

            String reason = booking.getCancellationReason();
            if (reasonContainer != null && cancellationReason != null) { // Kiểm tra null
                if (reason != null && !reason.isEmpty()) {
                    cancellationReason.setText(reason);
                    reasonContainer.setVisible(true);
                    reasonContainer.setManaged(true);
                } else {
                    cancellationReason.setText("Không có lý do được cung cấp.");
                    cancellationReason.setVisible(true); // Hoặc false nếu không muốn hiện dòng này
                    cancellationReason.setManaged(true); // Hoặc false
                }
            }



            // Hiển thị trạng thái
            String apiStatus = booking.getStatusBooking();
            String displayStatusText = apiStatus; // Mặc định là trạng thái từ API
            statusBookingText.getStyleClass().clear(); // Xóa style cũ

            // Chỉ xử lý cho trạng thái CANCELLED mà controller này đảm nhận
            if ("CANCELLED".equalsIgnoreCase(apiStatus)) {
                displayStatusText = "Đã hủy"; // SỬA: Hiển thị đúng là "Đã hủy"
                statusBookingText.getStyleClass().add("status-cancelled"); // Giữ style cho cancelled
                // Logic hiển thị cancelContainer và reasonContainer đã xử lý ở trên
            } else {
                // Trường hợp này không nên xảy ra nếu MyBookingsController chỉ gửi booking CANCELLED vào đây
                // Nhưng để phòng hờ, hiển thị trạng thái gốc và style mặc định
                displayStatusText = (apiStatus != null ? apiStatus : "Không rõ");
                statusBookingText.getStyleClass().add("status-default");
                // Nếu trạng thái không phải CANCELLED, ẩn thông tin hủy đi
                if (cancelContainer != null) {
                    cancelContainer.setVisible(false);
                    cancelContainer.setManaged(false);
                }
                if (reasonContainer != null) {
                    reasonContainer.setVisible(false);
                    reasonContainer.setManaged(false);
                }
            }
            statusBookingText.setText(displayStatusText);;

        } else {
            // Xử lý trường hợp booking là null
            nameBookingLabel.setText("Lỗi dữ liệu");
            if(purposeBookingText != null) {purposeBookingText.setText(""); purposeBookingText.setVisible(false); purposeBookingText.setManaged(false);}
            if(userNameText!=null) userNameText.setText("");
            if(timeRangeText!=null) timeRangeText.setText("");
            if(requestBookingText!=null) requestBookingText.setText("");
            if(equipmentContainer!=null) {equipmentContainer.setVisible(false); equipmentContainer.setManaged(false);}
            if(cancelContainer!=null) {cancelContainer.setVisible(false); cancelContainer.setManaged(false);}
            // if(reasonDisplayContainer!=null) {reasonDisplayContainer.setVisible(false); reasonDisplayContainer.setManaged(false);}
            if(statusBookingText!=null) statusBookingText.setText("N/A");
            if(cancelledByUserName != null) {  cancelledByUserName.setText(""); }
            if(cancellationReason != null) {  cancellationReason.setText("N/A"); }
        }
    }
}
