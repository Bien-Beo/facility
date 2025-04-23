package com.utc2.facilityui.controller;

import com.utc2.facilityui.model.CancellationRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class CancellationRequestsController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox requestContainer;
    @FXML private Label titleLabel;

    private final ObservableList<CancellationRequest> cancellationRequests = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText("Cancellation Status");
        scrollPane.setFitToWidth(true);
        loadCancellationRequests();
        displayCancellationRequestCards();
    }

    private void loadCancellationRequests() {
        cancellationRequests.addAll(
                new CancellationRequest("Courtyard | Celebration", "Party", LocalDateTime.now().plusDays(2), "10:00 AM - 12:00 PM", "Alice", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "Canceled due to weather"),
                new CancellationRequest("Courtyard | Celebration", "Party", LocalDateTime.now().plusDays(2), "10:00 AM - 12:00 PM", "Alice", LocalDateTime.now().minusDays(1), LocalDateTime.now(), "Canceled due to weather")
        );
    }

    private void displayCancellationRequestCards() {
        requestContainer.getChildren().clear();
        for (CancellationRequest request : cancellationRequests) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utc2/facilityui/component/cardCancellationRequest.fxml"));
                VBox card = loader.load();
                CancellationRequestCardController controller = loader.getController();
                controller.setData(request);

                controller.getAcceptButton().setOnAction(e -> handleAccept(request));
                controller.getRejectButton().setOnAction(e -> handleReject(request));

                requestContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(CancellationRequest request) {
        System.out.println("Accepted: " + request.getTitleFacility());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Accept");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to accept the cancellation for " + request.getTitleFacility() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancellationRequests.remove(request);
                displayCancellationRequestCards(); // Refresh the display
                // Implement logic to update the booking status in your data source
            }
        });
    }

    private void handleReject(CancellationRequest request) {
        System.out.println("Rejected: " + request.getTitleFacility());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Reject");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to reject the cancellation for " + request.getTitleFacility() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancellationRequests.remove(request);
                displayCancellationRequestCards(); // Refresh the display
                // Implement logic to update the booking status in your data source
            }
        });
    }
}
