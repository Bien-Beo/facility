package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ApprovalRequestCardController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Label purposeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label requestedByLabel;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private ApprovalRequest request;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic, if any
    }

    public void setData(ApprovalRequest request) {
        this.request = request;
        titleLabel.setText(request.getFacilityName());
        purposeLabel.setText("Purpose: " + request.getPurpose());
        dateLabel.setText("Date: " + request.getFormattedDate()); // Use formatted date
        timeLabel.setText("Time: " + request.getTimeRange());
        requestedByLabel.setText("Requested By: " + request.getRequestedBy() + " at " + request.getFormattedRequestedAt());
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        System.out.println("Accept clicked");
        // Implement your approval logic here
    }

    @FXML
    private void handleReject(ActionEvent event) {
        System.out.println("Reject clicked");
        // Implement your rejection logic here
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public Button getRejectButton() {
        return rejectButton;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }
}