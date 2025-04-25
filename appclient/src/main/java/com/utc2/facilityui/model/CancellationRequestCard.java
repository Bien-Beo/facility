package com.utc2.facilityui.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CancellationRequestCard extends VBox {

    // FXML injected fields for labels and buttons
    @FXML private Label titleLabel;
    @FXML private Label purposeLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label requestedByLabel;
    @FXML private Label cancellationRequestedAtLabel;
    @FXML private Label cancellationRemarkLabel;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    public CancellationRequestCard(CancellationRequest request) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardCancellationRequest.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setData(request);
    }

    public Button getAcceptButton() { return acceptButton; }
    public Button getRejectButton() { return rejectButton; }

    public void setData(CancellationRequest request) {
        titleLabel.setText(request.getTitleFacility());
        purposeLabel.setText("Purpose: " + request.getPurpose());
        dateLabel.setText("Date: " + request.getFormattedDate());
        timeLabel.setText("Time: " + request.getTimeRange());
        requestedByLabel.setText("Requested By: " + request.getRequestedBy() + " at " + request.getFormattedRequestedAt());
        cancellationRequestedAtLabel.setText("Cancellation Requested At: " + request.getFormattedCancellationRequestedAt());
        cancellationRemarkLabel.setText("Cancellation Remark: " + request.getCancellationRemark());
    }

    @FXML
    private void handleAccept() {
        // Implement accept cancellation logic
    }

    @FXML
    private void handleReject() {
        // Implement reject cancellation logic
    }
}