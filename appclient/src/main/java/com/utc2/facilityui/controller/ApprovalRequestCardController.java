package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ApprovalRequestCardController implements Initializable {

    @FXML private HBox approvalDetailCard;
    @FXML private Label roomNameLabel;
    @FXML private Label purposeBookingLabel;
    @FXML private Label bookedByUserLabel;
    @FXML private Label timeRangeLabel;
    @FXML private Label requestedAtLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea reasonTextArea;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;

    private ApprovalRequest currentApprovalRequest;

    private final ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptAction = new SimpleObjectProperty<>();
    private final ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectAction = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (approvalDetailCard != null) {
            approvalDetailCard.setUserData(this);
        } else {
            System.err.println("LỖI NGHIÊM TRỌNG: approvalDetailCard là null. Kiểm tra fx:id trong FXML card.");
        }

        if (btnApprove != null) {
            btnApprove.setOnAction(event -> {
                if (onAcceptAction.get() != null) onAcceptAction.get().handle(event);
            });
        }

        if (btnReject != null) {
            // Ban đầu, vô hiệu hóa nút Reject
            btnReject.setDisable(true);

            // Thêm listener cho reasonTextArea
            if (reasonTextArea != null) {
                reasonTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Kích hoạt nút Reject nếu TextArea không trống (sau khi trim)
                    btnReject.setDisable(newValue == null || newValue.trim().isEmpty());
                });
            } else {
                System.err.println("LỖI: reasonTextArea là null trong ApprovalRequestCardController. Kiểm tra fx:id.");
            }

            btnReject.setOnAction(event -> {
                // Kiểm tra lại một lần nữa ở đây (mặc dù nút đã được disable/enable)
                // để đảm bảo an toàn, mặc dù nếu nút disable thì sự kiện này không nên xảy ra.
                String reason = getRejectReason();
                if (reason.isEmpty()) {
                    // Hiển thị thông báo cho người dùng nếu bằng cách nào đó họ vẫn nhấn được nút khi lý do trống
                    // (Điều này không nên xảy ra nếu logic disable/enable hoạt động đúng)
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Thiếu thông tin");
                    alert.setHeaderText(null);
                    alert.setContentText("Vui lòng nhập lý do từ chối.");
                    alert.showAndWait();
                    return;
                }
                if (onRejectAction.get() != null) {
                    onRejectAction.get().handle(event);
                }
            });
        }
    }

    public void setData(ApprovalRequest request) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setDataInternal(request));
            return;
        }
        setDataInternal(request);
    }

    private void setDataInternal(ApprovalRequest requestData) {
        this.currentApprovalRequest = requestData;

        if (requestData == null) {
            clearLabels();
            return;
        }

        if (roomNameLabel != null) roomNameLabel.setText(getStringOrEmpty(requestData.getFacilityName()));
        if (purposeBookingLabel != null) purposeBookingLabel.setText(getStringOrEmpty(requestData.getPurpose()));
        if (bookedByUserLabel != null) bookedByUserLabel.setText(getStringOrEmpty(requestData.getRequestedBy()));

        String displayTimeAndDateInfo = "N/A";
        String formattedTimeRange = getStringOrEmpty(requestData.getTimeRange());
        String formattedDate = getStringOrEmpty(requestData.getFormattedDate());

        if (!formattedTimeRange.equals("N/A") && !formattedTimeRange.isEmpty()) {
            displayTimeAndDateInfo = formattedTimeRange;
            if (!formattedDate.equals("N/A") && !formattedDate.isEmpty()) {
                if (!formattedTimeRange.toLowerCase().contains("đến") &&
                        !formattedTimeRange.toLowerCase().contains(formattedDate.substring(0, Math.min(formattedDate.length(),3)).toLowerCase()) ) {
                    displayTimeAndDateInfo += ", " + formattedDate;
                }
            }
        } else if (!formattedDate.equals("N/A") && !formattedDate.isEmpty()) {
            displayTimeAndDateInfo = formattedDate;
        }
        if(timeRangeLabel != null) timeRangeLabel.setText(displayTimeAndDateInfo);

        if (requestedAtLabel != null) requestedAtLabel.setText(getStringOrEmpty(requestData.getFormattedRequestedAt()));
        if (statusLabel != null) {
            statusLabel.setText(getStringOrEmpty(requestData.getStatusDisplay()));
            updateStatusLabelStyle(requestData.getStatusKey());
        }
        if (reasonTextArea != null) {
            reasonTextArea.clear(); // Xóa lý do cũ
            // Sau khi clear, listener của reasonTextArea sẽ được kích hoạt,
            // và btnReject sẽ tự động được disable nếu reasonTextArea trống.
        }
    }

    private void clearLabels() {
        if(roomNameLabel != null) roomNameLabel.setText("Phòng: N/A");
        if(purposeBookingLabel != null) purposeBookingLabel.setText("Mục đích: N/A");
        if(bookedByUserLabel != null) bookedByUserLabel.setText("Người đặt: N/A");
        if(timeRangeLabel != null) timeRangeLabel.setText("Thời gian: N/A");
        if(requestedAtLabel != null) requestedAtLabel.setText("Yêu cầu lúc: N/A");
        if(statusLabel != null) {
            statusLabel.setText("N/A");
            updateStatusLabelStyle(null);
        }
        if(reasonTextArea != null) reasonTextArea.clear();
        if(btnReject != null) btnReject.setDisable(true); // Vô hiệu hóa nút reject khi clear
    }

    private void updateStatusLabelStyle(String statusKey) {
        if (statusLabel == null) return;
        String styleBackgroundColor = "#757575";
        String styleTextColor = "white";

        if (statusKey != null) {
            switch (statusKey.toUpperCase()) {
                case "PENDING_APPROVAL": styleBackgroundColor = "#FFB74D"; break;
                case "CONFIRMED": styleBackgroundColor = "#4CAF50"; break;
                case "REJECTED": case "CANCELLED": styleBackgroundColor = "#F44336"; break;
                case "IN_PROGRESS": styleBackgroundColor = "#2196F3"; break;
                case "COMPLETED": styleBackgroundColor = "#00BCD4"; break;
                case "OVERDUE": styleBackgroundColor = "#D32F2F"; break;
            }
        }
        statusLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-color: " + styleBackgroundColor + "; " +
                        "-fx-text-fill: " + styleTextColor + "; " +
                        "-fx-padding: 3px 8px; " +
                        "-fx-background-radius: 20px;"
        );
    }

    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptActionProperty() { return onAcceptAction;}
    public void setOnAcceptAction(javafx.event.EventHandler<ActionEvent> handler) { this.onAcceptAction.set(handler);}
    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectActionProperty() { return onRejectAction;}
    public void setOnRejectAction(javafx.event.EventHandler<ActionEvent> handler) { this.onRejectAction.set(handler);}

    public Button getAcceptButton() { return btnApprove; }
    public Button getRejectButton() { return btnReject; }
    public String getBookingId() { return (currentApprovalRequest != null) ? currentApprovalRequest.getBookingId() : null; }
    public String getRejectReason() { return reasonTextArea != null ? reasonTextArea.getText().trim() : ""; }
    private String getStringOrEmpty(String str) { return str != null && !str.isBlank() ? str : "N/A"; }

    // Thêm getter cho reasonTextArea để ApprovalRequestController (cha) có thể focus nếu cần
    public TextArea getReasonTextArea() {
        return reasonTextArea;
    }
}