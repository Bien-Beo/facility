package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ApprovalRequestCardController implements Initializable {

    @FXML private VBox rootCardPane;
    @FXML private Label titleLabel;
    @FXML private Label purposeLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label requestedByLabel;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    private ApprovalRequest request;
    private ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptAction = new SimpleObjectProperty<>();
    private ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectAction = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Giữ lại việc set UserData để controller chính có thể truy cập nút
        if (rootCardPane != null) rootCardPane.setUserData(this);
        else System.err.println("WARNING: rootCardPane is null in ApprovalRequestCardController. Check fx:id in FXML.");

        // Gán hành động mặc định (sẽ được ghi đè bởi controller chính)
        acceptButton.setOnAction(event -> { if (onAcceptAction.get() != null) onAcceptAction.get().handle(event); });
        rejectButton.setOnAction(event -> { if (onRejectAction.get() != null) onRejectAction.get().handle(event); });
    }

    /**
     * Gán dữ liệu từ ApprovalRequest vào các label trên card.
     * @param request Đối tượng chứa dữ liệu cần hiển thị.
     */
    public void setData(ApprovalRequest request) {
        // Giữ lại kiểm tra thread
        if (!Platform.isFxApplicationThread()) {
            // Không cần log warning nữa nếu đã xác định không phải lỗi thread
            // System.err.println("!!! WARNING: setData called on NON-JavaFX Application Thread...");
            Platform.runLater(() -> setDataInternal(request));
            return;
        }
        setDataInternal(request);
    }

    /**
     * Hàm nội bộ để thực hiện việc gán dữ liệu, đảm bảo chạy trên Fx Thread.
     * Đã xóa mã debug.
     */
    private void setDataInternal(ApprovalRequest request) {
        this.request = request;
        // Xóa log debug không cần thiết
        // System.out.println("--- ApprovalRequestCardController.setDataInternal() ---");
        // System.out.println("Received ApprovalRequest: " + (request != null ? request.toString() : "null"));

        if (request == null) {
            // Xóa log debug
            // System.out.println("  setDataInternal received null request, clearing labels.");
            if(titleLabel != null) titleLabel.setText("Error: Null data");
            if(purposeLabel != null) purposeLabel.setText("");
            if(dateLabel != null) dateLabel.setText("");
            if(timeLabel != null) timeLabel.setText("");
            if(requestedByLabel != null) requestedByLabel.setText("");
            // System.out.println("--- Finished setting data (null) ---");
            return;
        }

        // Gán giá trị - Sử dụng lại các hàm format
        String facility = request.getFacilityName();
        if(titleLabel != null) titleLabel.setText(facility != null ? facility : "");
        // else System.err.println("setDataInternal: titleLabel is NULL!"); // Xóa debug
        // System.out.println("  Setting titleLabel to: '" + facility + "'"); // Xóa debug

        String purpose = request.getPurpose();
        if(purposeLabel != null) purposeLabel.setText("Purpose: " + (purpose != null ? purpose : ""));
        // else System.err.println("setDataInternal: purposeLabel is NULL!"); // Xóa debug
        // System.out.println("  Setting purposeLabel to: 'Purpose: " + purpose + "'"); // Xóa debug

        // Sử dụng lại hàm format
        String formattedDate = request.getFormattedDate();
        if(dateLabel != null) dateLabel.setText("Date: " + formattedDate);
        // else System.err.println("setDataInternal: dateLabel is NULL!"); // Xóa debug
        // System.out.println("  Setting dateLabel to: 'Date: " + formattedDate + "'"); // Xóa debug

        String timeRange = request.getTimeRange();
        if(timeLabel != null) timeLabel.setText("Time: " + (timeRange != null ? timeRange : ""));
        // else System.err.println("setDataInternal: timeLabel is NULL!"); // Xóa debug
        // System.out.println("  Setting timeLabel to: 'Time: " + timeRange + "'"); // Xóa debug

        String requestedBy = request.getRequestedBy();
        // Sử dụng lại hàm format
        String formattedRequestedAt = request.getFormattedRequestedAt();
        String requestedByText = "Requested By: " + (requestedBy != null ? requestedBy : "?") + " at " + formattedRequestedAt;
        if(requestedByLabel != null) requestedByLabel.setText(requestedByText);
        // else System.err.println("setDataInternal: requestedByLabel is NULL!"); // Xóa debug
        // System.out.println("  Setting requestedByLabel to: '" + requestedByText + "'"); // Xóa debug

        // System.out.println("--- Finished setting data for card ---"); // Xóa debug
    }

    // Properties và getters/setters cho actions và buttons (giữ nguyên)
    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptActionProperty() { return onAcceptAction;}
    public void setOnAcceptAction(javafx.event.EventHandler<ActionEvent> handler) { this.onAcceptAction.set(handler);}
    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectActionProperty() { return onRejectAction;}
    public void setOnRejectAction(javafx.event.EventHandler<ActionEvent> handler) { this.onRejectAction.set(handler);}
    public Button getAcceptButton() { return acceptButton;}
    public Button getRejectButton() { return rejectButton;}
}