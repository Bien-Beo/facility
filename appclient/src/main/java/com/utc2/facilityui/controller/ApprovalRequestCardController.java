package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptAction = new SimpleObjectProperty<>();
    private ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectAction = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acceptButton.setOnAction(onAcceptActionProperty().get());
        rejectButton.setOnAction(onRejectActionProperty().get());
    }

    public void setData(ApprovalRequest request) {
        this.request = request;
        titleLabel.setText(request.getFacilityName());
        purposeLabel.setText("Purpose: " + request.getPurpose());
        dateLabel.setText("Date: " + request.getFormattedDate()); // Use formatted date
        timeLabel.setText("Time: " + request.getTimeRange());
        requestedByLabel.setText("Requested By: " + request.getRequestedBy() + " at " + request.getFormattedRequestedAt());
    }

    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onAcceptActionProperty() {
        return onAcceptAction;
    }

    public void setOnAcceptAction(javafx.event.EventHandler<ActionEvent> handler) {
        this.onAcceptAction.set(handler);
        acceptButton.setOnAction(handler);
    }

    public ObjectProperty<javafx.event.EventHandler<ActionEvent>> onRejectActionProperty() {
        return onRejectAction;
    }

    public void setOnRejectAction(javafx.event.EventHandler<ActionEvent> handler) {
        this.onRejectAction.set(handler);
        rejectButton.setOnAction(handler);
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        System.out.println("Accept clicked on card for: " + request.getFacilityName());
        if (onAcceptAction.get() != null) {
            onAcceptAction.get().handle(event);
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        System.out.println("Reject clicked on card for: " + request.getFacilityName());
        if (onRejectAction.get() != null) {
            onRejectAction.get().handle(event);
        }
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