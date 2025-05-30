package com.utc2.facilityui.controller.booking;

import com.utc2.facilityui.model.CancelBookingRequestData;
import com.utc2.facilityui.model.CardBooking;
import com.utc2.facilityui.service.BookingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Optional;

public class CardBookingController {

    @FXML
    private Button btnCancel;

    @FXML
    private TextArea cancellationReason; // Ô nhập lý do hủy

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

    private CardBooking booking;
    private BookingService bookingService;
    private MyBookingsController myBookingsController; // Tham chiếu đến MyBookingsController

    // Phương thức để MyBookingsController truyền instance của nó vào
    public void setMyBookingsController(MyBookingsController myBookingsController) {
        this.myBookingsController = myBookingsController;
    }

    public void setBooking(CardBooking booking) {
        this.booking = booking;
        if (this.bookingService == null) { // Khởi tạo BookingService nếu chưa có
            this.bookingService = new BookingService();
        }
        updateCard();
    }

    private void updateCard() {
        if (booking != null) {
            nameBookingLabel.setText(booking.getNameBooking() != null ? booking.getNameBooking() : "Chưa có tên phòng");
            userNameText.setText(booking.getUserName() != null ? booking.getUserName() : "Không rõ");

            if (booking.getPurposeBooking() != null && !booking.getPurposeBooking().isEmpty()) {
                purposeBookingText.setText(booking.getPurposeBooking());
                purposeContainer.setVisible(true);
                purposeContainer.setManaged(true);
            } else {
                purposeBookingText.setText("");
                purposeContainer.setVisible(false);
                purposeContainer.setManaged(false);
            }

            timeRangeText.setText(booking.getTimeRangeDisplay() != null ? booking.getTimeRangeDisplay() : "N/A");
            requestBookingText.setText(booking.getRequestBooking() != null ? booking.getRequestBooking() : "N/A");

            if (booking.getEquipmentsDisplay() != null && !booking.getEquipmentsDisplay().isEmpty()) {
                equipmentsListText.setText(booking.getEquipmentsDisplay());
                equipmentContainer.setVisible(true);
                equipmentContainer.setManaged(true);
            } else {
                equipmentsListText.setText("");
                equipmentContainer.setVisible(false);
                equipmentContainer.setManaged(false);
            }

            // Chỉ đặt là "Chờ duyệt" nếu trạng thái thực sự là PENDING_APPROVAL hoặc tương tự
            // Nếu không, trạng thái này sẽ được cập nhật bởi MyBookingsController khi tải lại
            if (booking.getStatusBooking() != null && booking.getStatusBooking().equalsIgnoreCase("PENDING_APPROVAL")) {
                statusBookingText.setText("Chờ duyệt");
                statusBookingText.getStyleClass().clear();
                statusBookingText.getStyleClass().add("status-pending");
            } else {
                // Nếu trạng thái khác, có thể để trống hoặc hiển thị trạng thái từ model
                // Tuy nhiên, card này chủ yếu dành cho PENDING_APPROVAL
                statusBookingText.setText(booking.getStatusBooking() != null ? booking.getStatusBooking() : "Chờ duyệt");
            }


            // Đảm bảo các control được kích hoạt khi card được cập nhật/tải lại
            btnCancel.setDisable(false);
            cancellationReason.setDisable(false);

            btnCancel.setVisible(true);
            btnCancel.setManaged(true);
            cancellationReason.setVisible(true);
            cancellationReason.setManaged(true);
            cancellationReason.clear();

        } else {
            nameBookingLabel.setText("Lỗi dữ liệu");
            userNameText.setText("N/A");
            purposeContainer.setVisible(false); purposeContainer.setManaged(false);
            timeRangeText.setText("N/A");
            requestBookingText.setText("N/A");
            equipmentContainer.setVisible(false); equipmentContainer.setManaged(false);
            statusBookingText.setText("N/A");
            btnCancel.setVisible(false); btnCancel.setManaged(false);
            cancellationReason.setVisible(false); cancellationReason.setManaged(false);
        }
    }

    @FXML
    private void handleCancelBooking() {
        String reason = cancellationReason.getText();
        if (reason == null || reason.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thiếu thông tin");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập lý do hủy đặt phòng.");
            alert.showAndWait();
            cancellationReason.requestFocus();
            return;
        }

        if (booking != null && booking.getBookingId() != null) {
            String finalReason = reason.trim();

            System.out.println("Yêu cầu hủy booking với ID: " + booking.getBookingId() + " với lý do: " + finalReason);

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Xác nhận hủy");
            confirmation.setHeaderText("Bạn có chắc chắn muốn hủy yêu cầu đặt phòng này không?");
            confirmation.setContentText("Phòng: " + booking.getNameBooking() +
                    "\nThời gian: " + booking.getTimeRangeDisplay() +
                    "\nLý do: " + finalReason);
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                btnCancel.setDisable(true);
                cancellationReason.setDisable(true);

                if (this.bookingService == null) {
                    this.bookingService = new BookingService();
                }

                CancelBookingRequestData cancelPayload = new CancelBookingRequestData(finalReason);

                new Thread(() -> {
                    try {
                        // Gọi service để hủy
                        bookingService.cancelBookingByUser(booking.getBookingId(), cancelPayload);
                        Platform.runLater(() -> {
                            // Không cần cập nhật UI cục bộ nữa vì MyBookingsController sẽ làm mới toàn bộ
                            // statusBookingText.setText("Đã hủy");
                            // statusBookingText.getStyleClass().clear();
                            // statusBookingText.getStyleClass().add("status-cancelled");
                            // btnCancel.setVisible(false);
                            // btnCancel.setManaged(false);
                            // cancellationReason.setVisible(false);
                            // cancellationReason.setManaged(false);

                            showSuccessAlert("Hủy thành công", "Yêu cầu đặt phòng ID: " + booking.getBookingId() + " đã được hủy. Danh sách sẽ được làm mới.");

                            // Gọi MyBookingsController để làm mới danh sách
                            // Đây là điểm mấu chốt để card được "biến đổi"
                            if (myBookingsController != null) {
                                System.out.println("CardBookingController: Gọi refreshBookingsListPublic() từ MyBookingsController.");
                                myBookingsController.refreshBookingsListPublic();
                            } else {
                                System.err.println("CardBookingController: MyBookingsController instance is null, không thể làm mới danh sách.");
                            }
                        });
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            showErrorAlert("Lỗi Hủy Đặt Phòng", "Không thể hủy yêu cầu: " + e.getMessage());
                            btnCancel.setDisable(false);
                            cancellationReason.setDisable(false);
                        });
                        e.printStackTrace();
                    }
                }).start();
            }
        } else {
            showErrorAlert("Lỗi", "Thông tin đặt phòng không hợp lệ để thực hiện hủy.");
            btnCancel.setDisable(false);
            cancellationReason.setDisable(false);
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}