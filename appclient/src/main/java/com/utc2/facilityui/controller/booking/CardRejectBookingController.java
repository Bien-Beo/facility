package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CardRejectBooking;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class CardRejectBookingController {

    @FXML private Label nameBookingLabel;
    @FXML private Text purposeBookingText;
    // FXML của bạn đặt fx:id="purposeContainer" cho HBox chứa nameBookingLabel và purposeBookingText
    // Tuy nhiên, để quản lý visibility của purpose một cách độc lập hơn,
    // titlePurposeContainer có thể là HBox bao quanh chỉ purpose nếu bạn muốn ẩn cả label "Mục đích"
    @FXML private HBox purposeContainer; // Khớp với fx:id trong FXML bạn gửi

    @FXML private Text userNameText;
    @FXML private HBox userContainer; // Khớp với fx:id

    @FXML private Text timeRangeText;
    @FXML private Text requestBookingText;

    @FXML private HBox equipmentContainer;
    @FXML private Label equipmentsStaticLabel; // fx:id này có trong FXML bạn gửi
    @FXML private Text equipmentsListText;

    @FXML private HBox statusContainer; // Khớp với fx:id
    @FXML private Text statusBookingText;

    // Đổi tên biến để khớp với fx:id="rejectedContainer" trong FXML bạn gửi
    @FXML private HBox rejectedContainer;  // Trước đây là processedByContainer
    // Label "Đã từ chối bởi:" trong FXML của bạn là tĩnh, không có fx:id="actionTypeLabel"
    // Nếu bạn muốn Label này thay đổi (ví dụ: "Đã hủy bởi"), bạn cần thêm fx:id cho nó.
    // Hiện tại, tôi sẽ giả định Label đó là tĩnh.
    @FXML private Text approvedByUserName;   // Tên người thực hiện

    // FXML bạn gửi chưa có HBox và Text cho lý do.
    // Nếu bạn thêm chúng với fx:id="reasonDisplayContainer" và fx:id="cancellationReasonDisplayText",
    // thì bỏ comment các dòng @FXML và logic liên quan dưới đây.
    // @FXML private HBox reasonDisplayContainer;
    // @FXML private Text cancellationReasonDisplayText;

    private CardRejectBooking booking;

    public void setRejectBooking(CardRejectBooking booking) {
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
            String actorName = booking.getApprovedByUserName();
            if (rejectedContainer != null) { // Kiểm tra null cho HBox
                if (actorName != null && !actorName.isEmpty()) {
                    approvedByUserName.setText(actorName);
                    rejectedContainer.setVisible(true);
                    rejectedContainer.setManaged(true);
                } else {
                    approvedByUserName.setText(""); // Hoặc "Không có thông tin"
                    rejectedContainer.setVisible(false);
                    rejectedContainer.setManaged(false);
                }
            }


            // Hiển thị lý do hủy/từ chối - BỎ COMMENT NẾU FXML CÓ CÁC fx:id NÀY
            /*
            String reason = booking.getCancellationReason();
            if (reasonDisplayContainer != null && cancellationReasonDisplayText != null) { // Kiểm tra null
                if (reason != null && !reason.isEmpty()) {
                    cancellationReasonDisplayText.setText(reason);
                    reasonDisplayContainer.setVisible(true);
                    reasonDisplayContainer.setManaged(true);
                } else {
                    cancellationReasonDisplayText.setText("Không có lý do được cung cấp.");
                    reasonDisplayContainer.setVisible(true); // Hoặc false nếu không muốn hiện dòng này
                    reasonDisplayContainer.setManaged(true); // Hoặc false
                }
            }
            */


            // Hiển thị trạng thái
            String apiStatus = booking.getStatusBooking();
            String displayStatusText = apiStatus;
            statusBookingText.getStyleClass().clear(); // Xóa style cũ

            // Vì FXML bạn gửi có label "Đã từ chối bởi:", chúng ta sẽ ưu tiên hiển thị cho REJECTED
            // và CANCELLED cũng sẽ hiển thị "Đã hủy" nhưng label "Đã từ chối bởi" có thể không đổi
            if ("REJECTED".equalsIgnoreCase(apiStatus)) {
                displayStatusText = "Bị từ chối";
                statusBookingText.getStyleClass().add("status-rejected");
                // Label tĩnh "Đã từ chối bởi:" trong FXML đã phù hợp
            } else {
                displayStatusText = (apiStatus != null ? apiStatus : "Không rõ");
                statusBookingText.getStyleClass().add("status-default");
                // Nếu rơi vào đây, ẩn cụm "Đã từ chối bởi" đi
                if (rejectedContainer != null) {
                    rejectedContainer.setVisible(false);
                    rejectedContainer.setManaged(false);
                }
            }
            statusBookingText.setText(displayStatusText);

        } else {
            // Xử lý trường hợp booking là null
            nameBookingLabel.setText("Lỗi dữ liệu");
            if(purposeBookingText != null) {purposeBookingText.setText(""); purposeBookingText.setVisible(false); purposeBookingText.setManaged(false);}
            if(userNameText!=null) userNameText.setText("");
            if(timeRangeText!=null) timeRangeText.setText("");
            if(requestBookingText!=null) requestBookingText.setText("");
            if(equipmentContainer!=null) {equipmentContainer.setVisible(false); equipmentContainer.setManaged(false);}
            if(rejectedContainer!=null) {rejectedContainer.setVisible(false); rejectedContainer.setManaged(false);}
            // if(reasonDisplayContainer!=null) {reasonDisplayContainer.setVisible(false); reasonDisplayContainer.setManaged(false);}
            if(statusBookingText!=null) statusBookingText.setText("N/A");
        }
    }
}