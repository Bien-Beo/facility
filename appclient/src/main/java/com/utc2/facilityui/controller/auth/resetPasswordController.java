package com.utc2.facilityui.controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class resetPasswordController {

    @FXML private TextField oldPasswordField;
    @FXML private TextField newPasswordField;
    @FXML private Label oldPasswordEye;
    @FXML private Label newPasswordEye;
    @FXML private StackPane oldPasswordPane;  // StackPane chứa oldPasswordField và eye icon
    @FXML private StackPane newPasswordPane;  // StackPane chứa newPasswordField và eye icon

    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;

    // Toggle visibility for old password field
    @FXML
    private void toggleOldPasswordVisibility() {
        if (isOldPasswordVisible) {
            // Chuyển lại thành PasswordField (ẩn mật khẩu)
            PasswordField newOldPasswordField = new PasswordField();
            newOldPasswordField.setText(oldPasswordField.getText());  // Preserve the text
            oldPasswordPane.getChildren().set(0, newOldPasswordField); // Thay thế PasswordField cũ
            oldPasswordField = newOldPasswordField;  // Cập nhật tham chiếu mới
        } else {
            // Hiển thị mật khẩu dưới dạng TextField
            TextField newOldPasswordField = new TextField();
            newOldPasswordField.setText(oldPasswordField.getText());  // Preserve the text
            oldPasswordPane.getChildren().set(0, newOldPasswordField); // Thay thế PasswordField cũ bằng TextField
            oldPasswordField = newOldPasswordField;  // Cập nhật tham chiếu mới
        }
        isOldPasswordVisible = !isOldPasswordVisible;
    }

    // Toggle visibility for new password field
    @FXML
    private void toggleNewPasswordVisibility() {
        if (isNewPasswordVisible) {
            // Chuyển lại thành PasswordField (ẩn mật khẩu)
            PasswordField newNewPasswordField = new PasswordField();
            newNewPasswordField.setText(newPasswordField.getText());  // Preserve the text
            newPasswordPane.getChildren().set(0, newNewPasswordField); // Thay thế PasswordField cũ
            newPasswordField = newNewPasswordField;  // Cập nhật tham chiếu mới
        } else {
            // Hiển thị mật khẩu dưới dạng TextField
            TextField newNewPasswordField = new TextField();
            newNewPasswordField.setText(newPasswordField.getText());  // Preserve the text
            newPasswordPane.getChildren().set(0, newNewPasswordField); // Thay thế PasswordField cũ bằng TextField
            newPasswordField = newNewPasswordField;  // Cập nhật tham chiếu mới
        }
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    // Handle the password change logic (e.g., send the request to the server)
    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        // Implement your logic to handle password change here
        // For example, send the passwords to the server or validate them
        System.out.println("Old Password: " + oldPassword);
        System.out.println("New Password: " + newPassword);
    }

    // Handle the eye icon click events
    @FXML
    private void handleOldPasswordEyeClick() {
        toggleOldPasswordVisibility();
    }

    @FXML
    private void handleNewPasswordEyeClick() {
        toggleNewPasswordVisibility();
    }
}
