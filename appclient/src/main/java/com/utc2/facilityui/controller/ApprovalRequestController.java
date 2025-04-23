package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import com.utc2.facilityui.controller.ApprovalRequestCardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ApprovalRequestController implements Initializable {

    @FXML
    private VBox requestContainer;

    private ObservableList<ApprovalRequest> approvalRequests = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadApprovalRequests();
        displayApprovalRequestCards();
    }

    private void loadApprovalRequests() {
        // Replace with your actual data retrieval logic
        approvalRequests.addAll(
                new ApprovalRequest("Facility A", "Event 1", LocalDateTime.now(), "10:00 - 12:00", "User 1", LocalDateTime.now()),
                new ApprovalRequest("Facility B", "Meeting", LocalDateTime.now(), "14:00 - 16:00", "User 2", LocalDateTime.now())
        );
    }

    private void displayApprovalRequestCards() {
        requestContainer.getChildren().clear(); // Clear previous cards if needed
        for (ApprovalRequest request : approvalRequests) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardApprovalRequest.fxml"));
                Parent card = loader.load();
                ApprovalRequestCardController cardController = loader.getController();
                cardController.setData(request);

                // Set the event handlers in the card controller to methods in this controller
                cardController.setOnAcceptAction(event -> handleAccept(request));
                cardController.setOnRejectAction(event -> handleReject(request));

                requestContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception
            }
        }
    }

    private void handleAccept(ApprovalRequest request) {
        System.out.println("Accepting request: " + request.getFacilityName());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Accept");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to accept the request for " + request.getFacilityName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                approvalRequests.remove(request);
                displayApprovalRequestCards(); // Refresh the UI
                // Implement logic to update the booking status in your data source
            }
        });
    }

    private void handleReject(ApprovalRequest request) {
        System.out.println("Rejecting request: " + request.getFacilityName());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Reject");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to reject the request for " + request.getFacilityName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                approvalRequests.remove(request);
                displayApprovalRequestCards(); // Refresh the UI
                // Implement logic to update the booking status in your data source
            }
        });
    }
}