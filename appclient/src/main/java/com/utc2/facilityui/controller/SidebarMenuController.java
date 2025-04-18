package com.utc2.facilityui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarMenuController implements Initializable {

    @FXML
    private Button manageFacilitiesButton;

    @FXML
    private Button manageBookingsButton;

    @FXML
    private Button approvalRequestsButton;

    @FXML
    private Label approvalCountLabel; // Added

    @FXML
    private Button cancellationRequestsButton;

    @FXML
    private Button reportButton;

    @FXML
    private Button resetPasswordButton;

    @FXML
    private Button logoutButton;

    private BorderPane mainBorderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic here, if needed
        setApprovalRequestCount(4); // Example: Set initial count
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    @FXML
    void handleManageFacilities(ActionEvent event) {
        System.out.println("Manage Facilities button clicked");
        loadView("/com/utc2/facilityui/view/manageFacility.fxml");
    }

    @FXML
    void handleManageBookings(ActionEvent event) {
        System.out.println("Manage Bookings button clicked");
        loadView("/com/utc2/facilityui/view/manageBookings.fxml");
    }

    @FXML
    void handleApprovalRequests(ActionEvent event) {
        System.out.println("Approval Requests button clicked");
        loadView("/com/utc2/facilityui/view/approvalrequests.fxml");
    }

    @FXML
    void handleCancellationRequests(ActionEvent event) {
        System.out.println("Cancellation Requests button clicked");
        loadView("/com/utc2/facilityui/view/cancellationRequests.fxml");
    }

    @FXML
    void handleReport(ActionEvent event) {
        System.out.println("Report button clicked");
        loadView("/com/utc2/facilityui/view/report.fxml");
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        System.out.println("Reset Password button clicked");
        loadView("/com/utc2/facilityui/view/resetPassword.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked");
        // Handle logout logic
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    public void setApprovalRequestCount(int count) {
        approvalCountLabel.setText(String.valueOf(count));
    }
}