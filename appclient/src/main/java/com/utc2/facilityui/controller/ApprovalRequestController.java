package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.ApprovalRequest;
import com.utc2.facilityui.controller.ApprovalRequestCardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ApprovalRequestController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox requestContainer;

    private ObservableList<ApprovalRequest> approvalRequests = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadApprovalRequests();
        displayApprovalRequests();
    }

    private void loadApprovalRequests() {
        // Replace with your actual data retrieval logic
        approvalRequests.addAll(
                new ApprovalRequest("Facility A", "Event 1", LocalDateTime.now(), "10:00 - 12:00", "User 1", LocalDateTime.now()),
                new ApprovalRequest("Facility B", "Meeting", LocalDateTime.now(), "14:00 - 16:00", "User 2", LocalDateTime.now())
        );
    }

    private void displayApprovalRequests() {
        try {
            for (ApprovalRequest request : approvalRequests) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardApprovalRequest.fxml"));
                Parent card = loader.load();
                ApprovalRequestCardController cardController = loader.getController();
                cardController.setData(request);
                requestContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    // Methods to handle approval/rejection actions
    // ...
}