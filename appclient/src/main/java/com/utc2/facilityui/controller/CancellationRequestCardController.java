package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CancellationRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CancellationRequestCardController {

    @FXML private Label titleLabel;
    @FXML private Label purposeLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label requestedByLabel;
    @FXML private Label cancellationRequestedAtLabel;
    @FXML private Label cancellationRemarkLabel;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    private CancellationRequest request;

    public void setData(CancellationRequest request) {
        this.request = request;
        titleLabel.setText(request.getTitleFacility());
        purposeLabel.setText(request.getPurpose());
        dateLabel.setText(request.getFormattedDate());
        timeLabel.setText(request.getTimeRange());
        requestedByLabel.setText(request.getRequestedBy() + " at " + request.getFormattedRequestedAt());
        cancellationRequestedAtLabel.setText(request.getFormattedCancellationRequestedAt());
        cancellationRemarkLabel.setText(request.getCancellationRemark());
    }

    public Button getAcceptButton() { return acceptButton; }
    public Button getRejectButton() { return rejectButton; }

    @FXML
    private void handleAccept() {
        System.out.println("Accepted: " + request.getTitleFacility());
    }

    @FXML
    private void handleReject() {
        System.out.println("Rejected: " + request.getTitleFacility());
    }
}
