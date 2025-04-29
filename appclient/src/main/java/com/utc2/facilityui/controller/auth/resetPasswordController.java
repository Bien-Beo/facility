package com.utc2.facilityui.controller.auth;

// Bỏ import DTO request: import com.utc2.facilityui.dto.request.PasswordResetRequest;
import com.utc2.facilityui.service.UserServices;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class resetPasswordController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label oldPasswordEye;
    @FXML private Label newPasswordEye;
    @FXML private StackPane oldPasswordPane;
    @FXML private StackPane newPasswordPane;
    @FXML private Label messageLabel;
    @FXML private Button confirmButton;

    private TextInputControl currentOldPasswordField;
    private TextInputControl currentNewPasswordField;
    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;

    @FXML
    public void initialize() {
        currentOldPasswordField = oldPasswordField;
        currentNewPasswordField = newPasswordField;
        messageLabel.setText("");
    }

    @FXML
    private void toggleOldPasswordVisibility(MouseEvent event) {
        String currentText = currentOldPasswordField.getText();
        Node nodeToReplace;
        if (isOldPasswordVisible) {
            PasswordField pf = new PasswordField(); pf.setText(currentText); pf.setPromptText("Old Password");
            nodeToReplace = pf; currentOldPasswordField = pf; oldPasswordEye.setText("👁");
        } else {
            TextField tf = new TextField(); tf.setText(currentText); tf.setPromptText("Old Password");
            nodeToReplace = tf; currentOldPasswordField = tf; oldPasswordEye.setText("");
        }
        isOldPasswordVisible = !isOldPasswordVisible;
        oldPasswordPane.getChildren().set(0, nodeToReplace);
    }

    @FXML
    private void toggleNewPasswordVisibility(MouseEvent event) {
        String currentText = currentNewPasswordField.getText();
        Node nodeToReplace;
        if (isNewPasswordVisible) {
            PasswordField pf = new PasswordField(); pf.setText(currentText); pf.setPromptText("New Password");
            nodeToReplace = pf; currentNewPasswordField = pf; newPasswordEye.setText("👁");
        } else {
            TextField tf = new TextField(); tf.setText(currentText); tf.setPromptText("New Password");
            nodeToReplace = tf; currentNewPasswordField = tf; newPasswordEye.setText("");
        }
        isNewPasswordVisible = !isNewPasswordVisible;
        newPasswordPane.getChildren().set(0, nodeToReplace);
    }

    @FXML
    private void handleChangePassword() {
        String oldPassword = currentOldPasswordField.getText();
        String newPassword = currentNewPasswordField.getText();

        // --- Validation ---
        if (oldPassword.isEmpty()) { showMessage("Old Password cannot be empty.", Color.RED); return; }
        if (newPassword.isEmpty()) { showMessage("New Password cannot be empty.", Color.RED); return; }
        if (newPassword.length() < 5) { showMessage("The new password must be minimum of 5 characters long.", Color.RED); return; }
        if (newPassword.equals(oldPassword)) { showMessage("New password must be different from the old password.", Color.RED); return; }
        // --- Hết Validation ---

        showMessage("", Color.BLACK);
        confirmButton.setDisable(true);
        confirmButton.setText("UPDATING...");

        // KHÔNG tạo DTO nữa
        // PasswordResetRequest request = PasswordResetRequest.builder()...

        Task<Boolean> resetTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Gọi service với hai chuỗi mật khẩu
                return UserServices.resetPassword(oldPassword, newPassword);
            }
        };

        resetTask.setOnSucceeded(event -> {
            boolean success = resetTask.getValue();
            if (success) {
                showMessage("Password updated successfully!", Color.GREEN);
                currentOldPasswordField.clear();
                currentNewPasswordField.clear();
                if (isOldPasswordVisible) toggleOldPasswordVisibility(null);
                if (isNewPasswordVisible) toggleNewPasswordVisibility(null);
            } else {
                // Ít xảy ra nếu service ném Exception
                showMessage("Password update failed. Please check details.", Color.RED);
            }
            confirmButton.setDisable(false);
            confirmButton.setText("CONFIRM");
        });

        resetTask.setOnFailed(event -> {
            Throwable exception = resetTask.getException();
            System.err.println("Password reset task failed: " + exception.getMessage());
            exception.printStackTrace();
            String errorMessage = exception.getMessage();
            // Cố gắng hiển thị lỗi cụ thể hơn
            if (errorMessage != null && errorMessage.toLowerCase().contains("incorrect old password")) {
                showMessage("Incorrect old password.", Color.RED);
            } else if (errorMessage != null && errorMessage.contains("HTTP status: 401")) { // Ví dụ bắt lỗi 401
                showMessage("Authentication error. Please log in again.", Color.RED);
            } else if (exception instanceof IOException) {
                showMessage("Error connecting to server. Please try again.", Color.RED);
            } else {
                showMessage("An unexpected error occurred: " + exception.getMessage(), Color.RED);
            }
            confirmButton.setDisable(false);
            confirmButton.setText("CONFIRM");
        });

        new Thread(resetTask).start();
    }

    private void showMessage(String message, Color color) {
        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setTextFill(color);
            messageLabel.setVisible(!message.isEmpty());
        });
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}